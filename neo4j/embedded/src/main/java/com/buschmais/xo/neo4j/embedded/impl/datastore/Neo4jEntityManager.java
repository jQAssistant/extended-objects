package com.buschmais.xo.neo4j.embedded.impl.datastore;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterator;

import com.buschmais.xo.api.ResultIterator;
import com.buschmais.xo.api.XOException;
import com.buschmais.xo.neo4j.embedded.impl.model.EmbeddedLabel;
import com.buschmais.xo.neo4j.embedded.impl.model.EmbeddedNode;
import com.buschmais.xo.neo4j.spi.metadata.NodeMetadata;
import com.buschmais.xo.neo4j.spi.metadata.PropertyMetadata;
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
public class Neo4jEntityManager extends AbstractNeo4jPropertyManager<EmbeddedNode>
        implements DatastoreEntityManager<Long, EmbeddedNode, NodeMetadata<EmbeddedLabel>, EmbeddedLabel, PropertyMetadata> {

    private final GraphDatabaseService graphDatabaseService;

    private final Cache<Long, Set<EmbeddedLabel>> labelCache;

    public Neo4jEntityManager(GraphDatabaseService graphDatabaseService) {
        this.graphDatabaseService = graphDatabaseService;
        this.labelCache = CacheBuilder.newBuilder().maximumSize(256).build();
    }

    @Override
    public boolean isEntity(Object o) {
        return EmbeddedNode.class.isAssignableFrom(o.getClass());
    }

    @Override
    public Set<EmbeddedLabel> getEntityDiscriminators(EmbeddedNode node) {
        Set<EmbeddedLabel> labels = labelCache.getIfPresent(node.getId());
        if (labels == null) {
            labels = new HashSet<>();
            for (EmbeddedLabel label : node.getLabels()) {
                labels.add(label);
            }
            labelCache.put(node.getId(), labels);
        }
        return labels;
    }

    @Override
    public Long getEntityId(EmbeddedNode entity) {
        return Long.valueOf(entity.getId());
    }

    @Override
    public EmbeddedNode createEntity(TypeMetadataSet<EntityTypeMetadata<NodeMetadata<EmbeddedLabel>>> types, Set<EmbeddedLabel> discriminators,
            Map<PrimitivePropertyMethodMetadata<PropertyMetadata>, Object> example) {
        Label[] labels = new Label[discriminators.size()];
        int i = 0;
        for (EmbeddedLabel discriminator : discriminators) {
            labels[i++] = discriminator.getDelegate();
        }
        EmbeddedNode node = new EmbeddedNode(graphDatabaseService.createNode(labels));
        setProperties(node, example);
        labelCache.put(node.getId(), discriminators);
        return node;
    }

    @Override
    public void deleteEntity(EmbeddedNode entity) {
        entity.delete();
        labelCache.invalidate(entity.getId());
    }

    @Override
    public EmbeddedNode findEntityById(EntityTypeMetadata<NodeMetadata<EmbeddedLabel>> metadata, EmbeddedLabel label, Long id) {
        return new EmbeddedNode(graphDatabaseService.getNodeById(id));
    }

    @Override
    public ResultIterator<EmbeddedNode> findEntity(EntityTypeMetadata<NodeMetadata<EmbeddedLabel>> entityTypeMetadata, EmbeddedLabel discriminator,
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
        ResourceIterator<Node> iterator = graphDatabaseService.findNodes(discriminator.getDelegate(), propertyMetadata.getName(), value);
        return new ResultIterator<EmbeddedNode>() {
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public EmbeddedNode next() {
                return new EmbeddedNode(iterator.next());
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
    public void migrateEntity(EmbeddedNode entity, TypeMetadataSet<EntityTypeMetadata<NodeMetadata<EmbeddedLabel>>> types, Set<EmbeddedLabel> discriminators,
            TypeMetadataSet<EntityTypeMetadata<NodeMetadata<EmbeddedLabel>>> targetTypes, Set<EmbeddedLabel> targetDiscriminators) {
        Set<EmbeddedLabel> labelsToRemove = new HashSet<>(discriminators);
        labelsToRemove.removeAll(targetDiscriminators);
        for (EmbeddedLabel label : labelsToRemove) {
            entity.removeLabel(label);
        }
        Set<EmbeddedLabel> labelsToAdd = new HashSet<>(targetDiscriminators);
        labelsToAdd.removeAll(discriminators);
        addDiscriminators(entity, labelsToAdd);
        labelCache.put(entity.getId(), targetDiscriminators);
    }

    @Override
    public void addDiscriminators(EmbeddedNode node, Set<EmbeddedLabel> labels) {
        for (EmbeddedLabel label : labels) {
            node.addLabel(label);
        }
        labelCache.invalidate(node.getId());
    }

    @Override
    public void removeDiscriminators(EmbeddedNode node, Set<EmbeddedLabel> labels) {
        for (EmbeddedLabel label : labels) {
            node.removeLabel(label);
        }
        labelCache.invalidate(node.getId());
    }

    @Override
    public void clear(Iterable<EmbeddedNode> nodes) {
        for (EmbeddedNode node : nodes) {
            node.clear();
            labelCache.invalidate(node.getId());
        }
    }

    @Override
    public void flush(Iterable<EmbeddedNode> nodes) {
        for (EmbeddedNode node : nodes) {
            node.flush();
            labelCache.invalidate(node.getId());
        }
    }
}
