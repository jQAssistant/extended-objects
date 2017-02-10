package com.buschmais.xo.neo4j.remote.impl;

import java.util.Map;
import java.util.Set;

import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;

import com.buschmais.xo.api.ResultIterator;
import com.buschmais.xo.neo4j.remote.impl.model.RemoteLabel;
import com.buschmais.xo.neo4j.remote.impl.model.RemoteNode;
import com.buschmais.xo.neo4j.spi.metadata.NodeMetadata;
import com.buschmais.xo.neo4j.spi.metadata.PropertyMetadata;
import com.buschmais.xo.spi.datastore.DatastoreEntityManager;
import com.buschmais.xo.spi.datastore.TypeMetadataSet;
import com.buschmais.xo.spi.metadata.method.PrimitivePropertyMethodMetadata;
import com.buschmais.xo.spi.metadata.type.EntityTypeMetadata;

public class RemoteDatastoreEntityManager extends RemoteDatastorePropertyManager<RemoteNode>
        implements DatastoreEntityManager<Long, RemoteNode, NodeMetadata<RemoteLabel>, RemoteLabel, PropertyMetadata> {

    public RemoteDatastoreEntityManager(Session session) {
        super(session);
    }

    @Override
    public boolean isEntity(Object o) {
        return RemoteNode.class.isAssignableFrom(o.getClass());
    }

    @Override
    public Set<RemoteLabel> getEntityDiscriminators(RemoteNode remoteNode) {
        return remoteNode.getLabels();
    }

    @Override
    public Long getEntityId(RemoteNode remoteNode) {
        return remoteNode.getId();
    }

    @Override
    public RemoteNode createEntity(TypeMetadataSet<EntityTypeMetadata<NodeMetadata<RemoteLabel>>> types, Set<RemoteLabel> remoteLabels,
            Map<PrimitivePropertyMethodMetadata<PropertyMetadata>, Object> exampleEntity) {
        StringBuilder labels = new StringBuilder();
        for (EntityTypeMetadata<NodeMetadata<RemoteLabel>> type : types) {
            labels.append(':').append(type.getDatastoreMetadata().getDiscriminator());
        }
        String statement = String.format("CREATE (n%s) RETURN id(n) as id", labels.toString());
        Record record = session.run(statement).single();
        long id = record.get("id").asLong();
        return new RemoteNode(id);
    }

    @Override
    public void deleteEntity(RemoteNode remoteNode) {

    }

    @Override
    public RemoteNode findEntityById(EntityTypeMetadata<NodeMetadata<RemoteLabel>> metadata, RemoteLabel remoteLabel, Long aLong) {
        return null;
    }

    @Override
    public ResultIterator<RemoteNode> findEntity(EntityTypeMetadata<NodeMetadata<RemoteLabel>> type, RemoteLabel remoteLabel,
            Map<PrimitivePropertyMethodMetadata<PropertyMetadata>, Object> values) {
        return null;
    }

    @Override
    public void migrateEntity(RemoteNode remoteNode, TypeMetadataSet<EntityTypeMetadata<NodeMetadata<RemoteLabel>>> types, Set<RemoteLabel> remoteLabels,
            TypeMetadataSet<EntityTypeMetadata<NodeMetadata<RemoteLabel>>> targetTypes, Set<RemoteLabel> targetDiscriminators) {

    }

    @Override
    public void addDiscriminators(RemoteNode remoteNode, Set<RemoteLabel> remoteLabels) {

    }

    @Override
    public void removeDiscriminators(RemoteNode remoteNode, Set<RemoteLabel> remoteLabels) {

    }

    @Override
    public void flushEntity(RemoteNode remoteNode) {

    }

    @Override
    public void clearEntity(RemoteNode remoteNode) {

    }
}
