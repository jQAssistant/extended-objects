package com.buschmais.xo.neo4j.remote.impl;

import static org.neo4j.driver.v1.Values.parameters;

import java.util.Map;

import org.neo4j.driver.v1.Record;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.neo4j.remote.impl.model.RemoteNode;
import com.buschmais.xo.neo4j.remote.impl.model.RemoteRelationship;
import com.buschmais.xo.neo4j.remote.impl.model.RemoteRelationshipType;
import com.buschmais.xo.neo4j.remote.impl.model.StatementExecutor;
import com.buschmais.xo.neo4j.remote.impl.model.state.RelationshipState;
import com.buschmais.xo.neo4j.spi.metadata.PropertyMetadata;
import com.buschmais.xo.neo4j.spi.metadata.RelationshipMetadata;
import com.buschmais.xo.spi.datastore.DatastoreRelationManager;
import com.buschmais.xo.spi.metadata.method.PrimitivePropertyMethodMetadata;
import com.buschmais.xo.spi.metadata.type.RelationTypeMetadata;

public class RemoteDatastoreRelationManager extends AbstractRemoteDatastorePropertyManager<RemoteRelationship, RelationshipState> implements
        DatastoreRelationManager<RemoteNode, Long, RemoteRelationship, RelationshipMetadata<RemoteRelationshipType>, RemoteRelationshipType, PropertyMetadata> {

    public RemoteDatastoreRelationManager(StatementExecutor statementExecutor, RemoteDatastoreSessionCache datastoreSessionCache) {
        super(statementExecutor, datastoreSessionCache);
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
        return new RemoteRelationship(id, relationshipState, source, remoteRelationshipType, target);
    }

    @Override
    public void deleteRelation(RemoteRelationship remoteRelationship) {
    }

    @Override
    public Long getRelationId(RemoteRelationship remoteRelationship) {
        return remoteRelationship.getId();
    }

    @Override
    public RemoteRelationship findRelationById(RelationTypeMetadata<RelationshipMetadata<RemoteRelationshipType>> metadata, Long id) {
        return null;
    }

    @Override
    public boolean hasSingleRelation(RemoteNode source, RelationTypeMetadata<RelationshipMetadata<RemoteRelationshipType>> metadata,
            RelationTypeMetadata.Direction direction) {
        return false;
    }

    @Override
    public RemoteRelationship getSingleRelation(RemoteNode source, RelationTypeMetadata<RelationshipMetadata<RemoteRelationshipType>> metadata,
            RelationTypeMetadata.Direction direction) {
        return null;
    }

    @Override
    public Iterable<RemoteRelationship> getRelations(RemoteNode source, RelationTypeMetadata<RelationshipMetadata<RemoteRelationshipType>> metadata,
            RelationTypeMetadata.Direction direction) {
        return null;
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
        return String.format("()-[%s]-()", identifier);
    }

    @Override
    protected RelationshipState load(RemoteRelationship entity) {
        return null;
    }
}
