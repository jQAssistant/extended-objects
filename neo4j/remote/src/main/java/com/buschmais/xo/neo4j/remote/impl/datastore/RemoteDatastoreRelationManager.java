package com.buschmais.xo.neo4j.remote.impl.datastore;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.api.metadata.method.PrimitivePropertyMethodMetadata;
import com.buschmais.xo.api.metadata.type.RelationTypeMetadata;
import com.buschmais.xo.neo4j.remote.impl.model.RemoteDirection;
import com.buschmais.xo.neo4j.remote.impl.model.RemoteNode;
import com.buschmais.xo.neo4j.remote.impl.model.RemoteRelationship;
import com.buschmais.xo.neo4j.remote.impl.model.RemoteRelationshipType;
import com.buschmais.xo.neo4j.remote.impl.model.state.RelationshipState;
import com.buschmais.xo.neo4j.remote.impl.model.state.StateTracker;
import com.buschmais.xo.neo4j.spi.metadata.PropertyMetadata;
import com.buschmais.xo.neo4j.spi.metadata.RelationshipMetadata;
import com.buschmais.xo.spi.datastore.DatastoreRelationManager;

import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Relationship;

import static org.neo4j.driver.Values.parameters;

public class RemoteDatastoreRelationManager extends AbstractRemoteDatastorePropertyManager<RemoteRelationship> implements
    DatastoreRelationManager<RemoteNode, Long, RemoteRelationship, RelationshipMetadata<RemoteRelationshipType>, RemoteRelationshipType, PropertyMetadata> {

    private long idSequence = -1;

    public RemoteDatastoreRelationManager(StatementExecutor statementExecutor, RemoteDatastoreSessionCache datastoreSessionCache) {
        super(statementExecutor, datastoreSessionCache);
    }

    @Override
    public boolean isRelation(Object o) {
        return RemoteRelationship.class.isAssignableFrom(o.getClass());
    }

    @Override
    public RemoteRelationshipType getRelationDiscriminator(RemoteRelationship remoteRelationship) {
        ensureLoaded(remoteRelationship);
        return remoteRelationship.getType();
    }

    @Override
    public RemoteRelationship createRelation(RemoteNode source, RelationTypeMetadata<RelationshipMetadata<RemoteRelationshipType>> metadata,
        RelationTypeMetadata.Direction direction, RemoteNode target, Map<PrimitivePropertyMethodMetadata<PropertyMetadata>, Object> exampleEntity) {
        RelationshipMetadata<RemoteRelationshipType> datastoreMetadata = metadata.getDatastoreMetadata();
        RemoteRelationshipType type = datastoreMetadata.getDiscriminator();
        RemoteNode start;
        RemoteNode end;
        RemoteDirection remoteDirection;
        RemoteDirection inverseRemoteDirection;
        switch (direction) {
        case FROM:
            start = source;
            end = target;
            remoteDirection = RemoteDirection.OUTGOING;
            inverseRemoteDirection = RemoteDirection.INCOMING;
            break;
        case TO:
            start = target;
            end = source;
            remoteDirection = RemoteDirection.INCOMING;
            inverseRemoteDirection = RemoteDirection.OUTGOING;
            break;
        default:
            throw new XOException("Unsupported direction " + direction);
        }
        Map<String, Object> properties = getProperties(exampleEntity);
        StateTracker<RemoteRelationship, Set<RemoteRelationship>> relationships = getRelationships(source, type, remoteDirection);
        RemoteRelationship relationship;
        if (!datastoreMetadata.isBatchable()) {
            // Create the relationship immediately
            String statement = String.format(
                "MATCH (start),(end) WHERE id(start)=$start and id(end)=$end CREATE (start)-[r:%s]->(end) SET r=$r RETURN id(r) as id", type.getName());
            Record record = statementExecutor.getSingleResult(statement, parameters("start", start.getId(), "end", end.getId(), "r", properties));
            long id = record.get("id")
                .asLong();
            RelationshipState relationshipState = new RelationshipState(properties);
            relationship = datastoreSessionCache.getRelationship(id, start, type, end, () -> relationshipState);
            relationships.getElements()
                .add(relationship);
        } else {
            // Defer creation of relationship to flush
            long id = idSequence--;
            relationship = datastoreSessionCache.getRelationship(id, start, type, end, () -> new RelationshipState(properties));
            relationships.add(relationship);
        }
        // Update inverse relation if it is already loaded
        StateTracker<RemoteRelationship, Set<RemoteRelationship>> inverseRelationships = target.getState()
            .getRelationships(inverseRemoteDirection, type);
        if (inverseRelationships != null) {
            inverseRelationships.getElements()
                .add(relationship);
        }
        return relationship;
    }

    @Override
    public void deleteRelation(RemoteRelationship remoteRelationship) {
        RemoteRelationshipType type = remoteRelationship.getType();
        RemoteNode startNode = remoteRelationship.getStartNode();
        RemoteNode endNode = remoteRelationship.getEndNode();
        StateTracker<RemoteRelationship, Set<RemoteRelationship>> outgoingRelationships = startNode.getState()
            .getRelationships(RemoteDirection.OUTGOING, type);
        StateTracker<RemoteRelationship, Set<RemoteRelationship>> incomingRelationships = endNode.getState()
            .getRelationships(RemoteDirection.INCOMING, type);
        // track change only on one relation side
        if (outgoingRelationships != null) {
            outgoingRelationships.remove(remoteRelationship);
        } else if (incomingRelationships != null) {
            incomingRelationships.remove(remoteRelationship);
        }
        if (outgoingRelationships != null) {
            outgoingRelationships.getElements()
                .remove(remoteRelationship);
        }
        if (incomingRelationships != null) {
            incomingRelationships.getElements()
                .remove(remoteRelationship);
        }
    }

    @Override
    public Long getRelationId(RemoteRelationship remoteRelationship) {
        return remoteRelationship.getId();
    }

    @Override
    public RemoteRelationship findRelationById(RelationTypeMetadata<RelationshipMetadata<RemoteRelationshipType>> metadata, Long id) {
        String statement = String.format("MATCH (start)-[r:%s]->(end) WHERE id(r)=$id RETURN start,r,end", metadata.getDatastoreMetadata()
            .getDiscriminator()
            .getName());
        Record record = statementExecutor.getSingleResult(statement, parameters("id", id));
        Node start = record.get("start")
            .asNode();
        Relationship relationship = record.get("r")
            .asRelationship();
        Node end = record.get("end")
            .asNode();
        return datastoreSessionCache.getRelationship(start, relationship, end);
    }

    @Override
    public RemoteRelationship getSingleRelation(RemoteNode source, RelationTypeMetadata<RelationshipMetadata<RemoteRelationshipType>> metadata,
        RelationTypeMetadata.Direction direction) {
        Iterator<RemoteRelationship> iterator = getSingleRelationship(source, metadata, direction).iterator();
        return iterator.hasNext() ? iterator.next() : null;
    }

    @Override
    public Iterable<RemoteRelationship> getRelations(RemoteNode source, RelationTypeMetadata<RelationshipMetadata<RemoteRelationshipType>> metadata,
        RelationTypeMetadata.Direction direction) {
        return getRelationships(source, metadata.getDatastoreMetadata()
            .getDiscriminator(), getRemoteDirection(direction)).getElements();
    }

    @Override
    public RemoteNode getFrom(RemoteRelationship remoteRelationship) {
        return remoteRelationship.getStartNode();
    }

    @Override
    public RemoteNode getTo(RemoteRelationship remoteRelationship) {
        return remoteRelationship.getEndNode();
    }

    @Override
    protected void load(RemoteRelationship propertyContainer) {
        Record record = statementExecutor.getSingleResult("MATCH ()-[r]->() WHERE id(r)=$id RETURN r", parameters("id", propertyContainer.getId()));
        datastoreSessionCache.getRelationship(record.get("r")
            .asRelationship());
    }

    private Set<RemoteRelationship> getSingleRelationship(RemoteNode source, RelationTypeMetadata<RelationshipMetadata<RemoteRelationshipType>> metadata,
        RelationTypeMetadata.Direction direction) {
        Set<RemoteRelationship> relationships = getRelationships(source, metadata.getDatastoreMetadata()
            .getDiscriminator(), getRemoteDirection(direction)).getElements();
        if (relationships.size() > 1) {
            throw new XOException("Found more than one relationship for node=" + source + ", type=" + metadata.getDatastoreMetadata()
                .getDiscriminator() + ", direction=" + direction + ", relationships=" + relationships);
        }
        return relationships;
    }

    @Override
    public void flush(Iterable<RemoteRelationship> relationships) {
        try (StatementBatchBuilder batchBuilder = new StatementBatchBuilder(statementExecutor)) {
            for (RemoteRelationship relationship : relationships) {
                flush(batchBuilder, relationship, "()-[r]->()", "r");
                relationship.getState()
                    .flush();
            }
        }
    }

    private StateTracker<RemoteRelationship, Set<RemoteRelationship>> getRelationships(RemoteNode source, RemoteRelationshipType type,
        RemoteDirection remoteDirection) {
        StateTracker<RemoteRelationship, Set<RemoteRelationship>> trackedRelationships = source.getState()
            .getRelationships(remoteDirection, type);
        if (trackedRelationships == null) {
            String sourceIdentifier;
            switch (remoteDirection) {
            case OUTGOING:
                sourceIdentifier = "start";
                break;
            case INCOMING:
                sourceIdentifier = "end";
                break;
            default:
                throw new XOException("Direction not supported: " + remoteDirection);
            }
            String statement = String.format("MATCH (start)-[r:%s]->(end) WHERE id(%s)=$id RETURN start,r,end", type.getName(), sourceIdentifier);
            Result statementResult = statementExecutor.execute(statement, parameters("id", source.getId()));
            Set<RemoteRelationship> loaded = new LinkedHashSet<>();
            try {
                while (statementResult.hasNext()) {
                    Record record = statementResult.next();
                    Node start = record.get("start")
                        .asNode();
                    Relationship relationship = record.get("r")
                        .asRelationship();
                    Node end = record.get("end")
                        .asNode();
                    RemoteRelationship remoteRelationship = datastoreSessionCache.getRelationship(start, relationship, end);
                    loaded.add(remoteRelationship);
                }
            } finally {
                statementResult.consume();
            }
            trackedRelationships = new StateTracker<>(loaded);
            source.getState()
                .setRelationships(remoteDirection, type, trackedRelationships);
        }
        return trackedRelationships;
    }

    private RemoteDirection getRemoteDirection(RelationTypeMetadata.Direction direction) {
        switch (direction) {
        case FROM:
            return RemoteDirection.OUTGOING;
        case TO:
            return RemoteDirection.INCOMING;
        default:
            throw new XOException("Direction not supported " + direction);
        }
    }

}
