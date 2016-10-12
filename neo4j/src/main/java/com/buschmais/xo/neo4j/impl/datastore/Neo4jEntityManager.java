package com.buschmais.xo.neo4j.impl.datastore;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterator;

import com.buschmais.xo.api.ResultIterator;
import com.buschmais.xo.api.XOException;
import com.buschmais.xo.neo4j.api.Neo4jLabel;
import com.buschmais.xo.neo4j.impl.datastore.metadata.NodeMetadata;
import com.buschmais.xo.neo4j.impl.datastore.metadata.PropertyMetadata;
import com.buschmais.xo.spi.datastore.DatastoreEntityManager;
import com.buschmais.xo.spi.datastore.TypeMetadataSet;
import com.buschmais.xo.spi.metadata.method.IndexedPropertyMethodMetadata;
import com.buschmais.xo.spi.metadata.method.PrimitivePropertyMethodMetadata;
import com.buschmais.xo.spi.metadata.type.EntityTypeMetadata;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * Implementation of a
 * {@link com.buschmais.xo.spi.datastore.DatastoreEntityManager} for Neo4j.
 */
public class Neo4jEntityManager extends AbstractNeo4jPropertyManager<Node>
        implements DatastoreEntityManager<Long, Node, NodeMetadata, Neo4jLabel, PropertyMetadata> {

    private final GraphDatabaseService graphDatabaseService;

    private final Cache<Long, Set<Neo4jLabel>> labelCache;

    public Neo4jEntityManager(GraphDatabaseService graphDatabaseService) {
        this.graphDatabaseService = graphDatabaseService;
        this.labelCache = CacheBuilder.newBuilder().maximumSize(256).build();
    }

    @Override
    public boolean isEntity(Object o) {
        return Node.class.isAssignableFrom(o.getClass());
    }

    @Override
    public Set<Neo4jLabel> getEntityDiscriminators(Node node) {
        Set<Neo4jLabel> labels = labelCache.getIfPresent(node.getId());
        if (labels == null) {
            labels = new HashSet<>();
            for (Label label : node.getLabels()) {
                labels.add(new Neo4jLabel(label));
            }
            labelCache.put(node.getId(), labels);
        }
        return labels;
    }

    @Override
    public Long getEntityId(Node entity) {
        return Long.valueOf(entity.getId());
    }

    @Override
    public Node createEntity(TypeMetadataSet<EntityTypeMetadata<NodeMetadata>> types, Set<Neo4jLabel> discriminators,
            Map<PrimitivePropertyMethodMetadata<PropertyMetadata>, Object> example) {
        Label[] labels = new Label[discriminators.size()];
        int i = 0;
        for (Neo4jLabel discriminator : discriminators) {
            labels[i++] = discriminator.getLabel();
        }
        Node node = graphDatabaseService.createNode(labels);
        setProperties(node, example);
        labelCache.put(node.getId(), discriminators);
        return node;
    }

    @Override
    public void deleteEntity(Node entity) {
        entity.delete();
        labelCache.invalidate(entity.getId());
    }

    @Override
    public Node findEntityById(EntityTypeMetadata<NodeMetadata> metadata, Neo4jLabel label, Long id) {
        return graphDatabaseService.getNodeById(id);
    }

    @Override
    public ResultIterator<Node> findEntity(EntityTypeMetadata<NodeMetadata> entityTypeMetadata, Neo4jLabel discriminator,
            Map<PrimitivePropertyMethodMetadata<PropertyMetadata>, Object> values) {
        if (values.size() > 1) {
            throw new XOException("Only one property value is supported for find operation");
        }
        Map.Entry<PrimitivePropertyMethodMetadata<PropertyMetadata>, Object> entry = values.entrySet().iterator().next();
        PrimitivePropertyMethodMetadata<PropertyMetadata> propertyMethodMetadata = entry.getKey();
        if (propertyMethodMetadata == null) {
            IndexedPropertyMethodMetadata<?> indexedProperty = entityTypeMetadata.getDatastoreMetadata().getUsingIndexedPropertyOf();
            if (indexedProperty == null) {
                throw new XOException("Type " + entityTypeMetadata.getAnnotatedType().getAnnotatedElement().getName() + " has no indexed property.");
            }
            propertyMethodMetadata = indexedProperty.getPropertyMethodMetadata();
        }
        PropertyMetadata propertyMetadata = propertyMethodMetadata.getDatastoreMetadata();
        Object value = entry.getValue();
        ResourceIterator<Node> iterator = graphDatabaseService.findNodes(discriminator.getLabel(), propertyMetadata.getName(), value);
        return new ResultIterator<Node>() {
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public Node next() {
                return iterator.next();
            }

            @Override
            public void remove() {
                throw new XOException("Remove operation is not supported for find results.");
            }

            @Override
            public void close() {
                iterator.close();
            }
        };
    }

    @Override
    public void migrateEntity(Node entity, TypeMetadataSet<EntityTypeMetadata<NodeMetadata>> types, Set<Neo4jLabel> discriminators,
            TypeMetadataSet<EntityTypeMetadata<NodeMetadata>> targetTypes, Set<Neo4jLabel> targetDiscriminators) {
        Set<Neo4jLabel> labelsToRemove = new HashSet<>(discriminators);
        labelsToRemove.removeAll(targetDiscriminators);
        for (Neo4jLabel label : labelsToRemove) {
            entity.removeLabel(label.getLabel());
        }
        Set<Neo4jLabel> labelsToAdd = new HashSet<>(targetDiscriminators);
        labelsToAdd.removeAll(discriminators);
        addDiscriminators(entity, labelsToAdd);
        labelCache.put(entity.getId(), targetDiscriminators);
    }

    @Override
    public void addDiscriminators(Node node, Set<Neo4jLabel> labels) {
        for (Neo4jLabel label : labels) {
            node.addLabel(label.getLabel());
        }
        labelCache.invalidate(node.getId());
    }

    @Override
    public void removeDiscriminators(Node node, Set<Neo4jLabel> labels) {
        for (Neo4jLabel label : labels) {
            node.removeLabel(label.getLabel());
        }
        labelCache.invalidate(node.getId());
    }

    @Override
    public void flushEntity(Node node) {
        labelCache.invalidate(node.getId());
    }

}
