package com.buschmais.xo.neo4j.remote.impl;

import java.util.HashMap;
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

public class RemoteDatastoreEntityManager extends AbstractRemoteDatastorePropertyManager<RemoteNode>
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
        for (RemoteLabel remoteLabel : remoteLabels) {
            labels.append(':').append(remoteLabel.getName());
        }
        String statement = String.format("CREATE (n%s) RETURN id(n) as id", labels.toString());
        Record record = session.run(statement).single();
        long id = record.get("id").asLong();
        RemoteNode remoteNode = new RemoteNode(id);
        remoteNode.getLabels().addAll(remoteLabels);
        return remoteNode;
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
    public void flush(Iterable<RemoteNode> entities) {
        StringBuilder statement = new StringBuilder();
        int i = 0;
        Map<String, Object> parameters = new HashMap<>();
        for (RemoteNode entity : entities) {
            Map<String, Object> writeCache = entity.getWriteCache();
            if (writeCache != null && !writeCache.isEmpty()) {
                String nodeIdentifier = "n" + i;
                parameters.put(nodeIdentifier, entity.getId());
                statement.append(String.format("MATCH (%s) WHERE id(%s)={%s} SET ", nodeIdentifier, nodeIdentifier, nodeIdentifier));
                for (Map.Entry<String, Object> entry : writeCache.entrySet()) {
                    String property = entry.getKey();
                    Object value = entry.getValue();
                    String parameterName = nodeIdentifier + '_' + property;
                    statement.append(nodeIdentifier).append('.').append(property).append('=').append('{').append(parameterName).append('}');
                    parameters.put(parameterName, value);
                }
                statement.append("\n");
                i++;
            }
        }
        if (statement.length() > 0) {
            statement.append("RETURN count(*) as nodes");
            Record record = session.run(statement.toString(), parameters).single();
            long nodes = record.get("nodes").asLong();
        }
    }

    @Override
    public void clear(Iterable<RemoteNode> entities) {
    }
}
