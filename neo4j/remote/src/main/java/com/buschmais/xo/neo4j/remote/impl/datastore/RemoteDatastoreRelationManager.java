package com.buschmais.xo.neo4j.remote.impl.datastore;

import static org.neo4j.driver.v1.Values.parameters;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Relationship;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.neo4j.remote.impl.model.*;
import com.buschmais.xo.neo4j.remote.impl.model.state.NodeState;
import com.buschmais.xo.neo4j.remote.impl.model.state.RelationshipState;
import com.buschmais.xo.neo4j.spi.metadata.PropertyMetadata;
import com.buschmais.xo.neo4j.spi.metadata.RelationshipMetadata;
import com.buschmais.xo.spi.datastore.DatastoreRelationManager;
import com.buschmais.xo.spi.metadata.method.PrimitivePropertyMethodMetadata;
import com.buschmais.xo.spi.metadata.type.RelationTypeMetadata;

public class RemoteDatastoreRelationManager extends AbstractRemoteDatastorePropertyManager<RemoteRelationship, RelationshipState> implements
        DatastoreRelationManager<RemoteNode, Long, RemoteRelationship, RelationshipMetadata<RemoteRelationshipType>, RemoteRelationshipType, PropertyMetadata> {

    private RemoteDatastoreEntityManager entityManager;

    public RemoteDatastoreRelationManager(RemoteDatastoreEntityManager entityManager, StatementExecutor statementExecutor,
            RemoteDatastoreSessionCache datastoreSessionCache) {
        super(statementExecutor, datastoreSessionCache);
        this.entityManager = entityManager;
    }

    @Override
    public boolean isRelation(Object o) {
        return RemoteRelationship.class.isAssignableFrom(o.getClass());
    }

    @Override
    public RemoteRelationshipType getRelationDiscriminator(RemoteRelationship remoteRelationship) {
        return remoteRelationship.getType();
    }

    @Override
    public RemoteRelationship createRelation(RemoteNode source, RelationTypeMetadata<RelationshipMetadata<RemoteRelationshipType>> metadata,
            RelationTypeMetadata.Direction direction, RemoteNode target, Map<PrimitivePropertyMethodMetadata<PropertyMetadata>, Object> exampleEntity) {
        RemoteRelationshipType remoteRelationshipType = metadata.getDatastoreMetadata().getDiscriminator();
        String statement = String.format(
                "MATCH (start),(end) WHERE id(start)={start} and id(end)={end} CREATE (start)-[r:%s]->(end) SET r={r} RETURN id(r) as id",
                remoteRelationshipType.getName());
        Map<String, Object> properties = getProperties(exampleEntity);
        RemoteNode start;
        RemoteNode end;
        switch (direction) {
        case FROM:
            start = source;
            end = target;
            break;
        case TO:
            start = target;
            end = source;
            break;
        default:
            throw new XOException("Unsupported direction " + direction);
        }
        Record record = statementExecutor.getSingleResult(statement, parameters("start", start.getId(), "end", end.getId(), "r", properties));
        long id = record.get("id").asLong();
        RelationshipState relationshipState = new RelationshipState(properties);
        return datastoreSessionCache.getRelationship(id, start, remoteRelationshipType, end, relationshipState);
    }

    @Override
    public void deleteRelation(RemoteRelationship remoteRelationship) {
        String statement = String.format("MATCH ()-[r:%s]->() WHERE id(r)={id} DELETE r RETURN count(r) as count", remoteRelationship.getType().getName());
        Record record = statementExecutor.getSingleResult(statement, parameters("id", remoteRelationship.getId()));
        long count = record.get("count").asLong();
        if (count != 1) {
            throw new XOException("Could not delete " + remoteRelationship);
        }
        RemoteNode startNode = remoteRelationship.getStartNode();
        NodeState state = startNode.getState();
        if (state != null) {
            state.getRelationships(RemoteDirection.OUTGOING, remoteRelationship.getType()).remove(remoteRelationship);
        }
    }

    @Override
    public Long getRelationId(RemoteRelationship remoteRelationship) {
        return remoteRelationship.getId();
    }

    @Override
    public RemoteRelationship findRelationById(RelationTypeMetadata<RelationshipMetadata<RemoteRelationshipType>> metadata, Long id) {
        String statement = String.format("MATCH (start)-[r:%s]->(end) WHERE id(r)={id} RETURN start,r,end", metadata.getDatastoreMetadata().getDiscriminator().getName());
        Record record = statementExecutor.getSingleResult(statement, parameters("id", id));
        Node start = record.get("start").asNode();
        Relationship relationship = record.get("r").asRelationship();
        Node end = record.get("end").asNode();
        return datastoreSessionCache.getRelationship(start, relationship, end);
    }

    @Override
    public boolean hasSingleRelation(RemoteNode source, RelationTypeMetadata<RelationshipMetadata<RemoteRelationshipType>> metadata,
            RelationTypeMetadata.Direction direction) {
        return !getSingleRelationship(source, metadata, direction).isEmpty();
    }

    @Override
    public RemoteRelationship getSingleRelation(RemoteNode source, RelationTypeMetadata<RelationshipMetadata<RemoteRelationshipType>> metadata,
            RelationTypeMetadata.Direction direction) {
        return getSingleRelationship(source, metadata, direction).iterator().next();
    }

    @Override
    public Iterable<RemoteRelationship> getRelations(RemoteNode source, RelationTypeMetadata<RelationshipMetadata<RemoteRelationshipType>> metadata,
            RelationTypeMetadata.Direction direction) {
        return getRelationships(source, metadata, direction);
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
    protected String createIdentifier(int i) {
        return "r" + i;
    }

    @Override
    protected String createIdentifierPattern(String identifier) {
        return String.format("()-[%s]->()", identifier);
    }

    @Override
    protected RelationshipState load(RemoteRelationship entity) {
        Relationship relationship = fetch(entity.getId());
        return datastoreSessionCache.getRelationshipState(relationship);
    }

    private Relationship fetch(Long id) {
        Record record = statementExecutor.getSingleResult("MATCH ()-[r]->() WHERE id(r)={id} RETURN r", parameters("id", id));
        return record.get("r").asRelationship();
    }

    private Set<RemoteRelationship> getSingleRelationship(RemoteNode source, RelationTypeMetadata<RelationshipMetadata<RemoteRelationshipType>> metadata,
            RelationTypeMetadata.Direction direction) {
        Set<RemoteRelationship> relationships = getRelationships(source, metadata, direction);
        if (relationships.size() > 1) {
            throw new XOException("Found more than one relationship for node=" + source + ", type=" + metadata.getDatastoreMetadata().getDiscriminator()
                    + ", direction=" + direction);
        }
        return relationships;
    }

    private Set<RemoteRelationship> getRelationships(RemoteNode source, RelationTypeMetadata<RelationshipMetadata<RemoteRelationshipType>> metadata,
            RelationTypeMetadata.Direction direction) {
        entityManager.ensureLoaded(source);
        RemoteDirection remoteDirection = getRemoteDirection(direction);
        RemoteRelationshipType type = metadata.getDatastoreMetadata().getDiscriminator();
        Set<RemoteRelationship> relationships = source.getState().getRelationships(remoteDirection, type);
        if (relationships == null) {
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
            String statement = String.format("MATCH (start)-[r:%s]->(end) WHERE id(%s)={id} RETURN start,r,end", type.getName(), sourceIdentifier);
            StatementResult statementResult = statementExecutor.execute(statement, parameters("id", source.getId()));
            relationships = new LinkedHashSet<>();
            try {
                while (statementResult.hasNext()) {
                    Record record = statementResult.next();
                    Node start = record.get("start").asNode();
                    Relationship relationship = record.get("r").asRelationship();
                    Node end = record.get("end").asNode();
                    relationships.add(datastoreSessionCache.getRelationship(start, relationship, end));
                }
            } finally {
                statementResult.consume();
            }
            source.getState().setRelationships(remoteDirection, type, relationships);
        }
        return relationships;
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
