package com.buschmais.xo.neo4j.remote.impl;

import java.util.Map;

import org.neo4j.driver.v1.Session;

import com.buschmais.xo.neo4j.remote.impl.model.RemoteNode;
import com.buschmais.xo.neo4j.remote.impl.model.RemoteRelationship;
import com.buschmais.xo.neo4j.remote.impl.model.RemoteRelationshipType;
import com.buschmais.xo.neo4j.spi.metadata.PropertyMetadata;
import com.buschmais.xo.neo4j.spi.metadata.RelationshipMetadata;
import com.buschmais.xo.spi.datastore.DatastoreRelationManager;
import com.buschmais.xo.spi.metadata.method.PrimitivePropertyMethodMetadata;
import com.buschmais.xo.spi.metadata.type.RelationTypeMetadata;

public class RemoteDatastoreRelationManager extends AbstractRemoteDatastorePropertyManager<RemoteRelationship> implements
        DatastoreRelationManager<RemoteNode, Long, RemoteRelationship, RelationshipMetadata<RemoteRelationshipType>, RemoteRelationshipType, PropertyMetadata> {

    public RemoteDatastoreRelationManager(Session session) {
        super(session);
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
        return null;
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
    public void flush(Iterable<RemoteRelationship> entities) {
    }

    @Override
    public void clear(Iterable<RemoteRelationship> entities) {
    }
}
