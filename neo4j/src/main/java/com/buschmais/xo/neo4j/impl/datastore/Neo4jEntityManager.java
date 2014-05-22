package com.buschmais.xo.neo4j.impl.datastore;

import com.buschmais.xo.api.ResultIterator;
import com.buschmais.xo.api.XOException;
import com.buschmais.xo.neo4j.impl.datastore.metadata.NodeMetadata;
import com.buschmais.xo.neo4j.impl.datastore.metadata.PropertyMetadata;
import com.buschmais.xo.spi.datastore.DatastoreEntityManager;
import com.buschmais.xo.spi.datastore.TypeMetadataSet;
import com.buschmais.xo.spi.metadata.method.IndexedPropertyMethodMetadata;
import com.buschmais.xo.spi.metadata.method.PrimitivePropertyMethodMetadata;
import com.buschmais.xo.spi.metadata.type.EntityTypeMetadata;
import org.neo4j.graphdb.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of a {@link com.buschmais.xo.spi.datastore.DatastoreEntityManager} for Neo4j.
 */
public class Neo4jEntityManager extends AbstractNeo4jPropertyManager<Node> implements DatastoreEntityManager<Long, Node, NodeMetadata, Label, PropertyMetadata> {

    private final GraphDatabaseService graphDatabaseService;

    private final Map<Long, Set<Label>> labelCache = new HashMap<>();

    public Neo4jEntityManager(GraphDatabaseService graphDatabaseService) {
        this.graphDatabaseService = graphDatabaseService;
    }

    @Override
    public boolean isEntity(Object o) {
        return Node.class.isAssignableFrom(o.getClass());
    }

    @Override
    public Set<Label> getEntityDiscriminators(Node node) {
        Set<Label> labels = labelCache.get(node.getId());
        if (labels == null) {
            labels = new HashSet<>();
            for (Label label : node.getLabels()) {
                labels.add(label);
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
    public Node createEntity(TypeMetadataSet<EntityTypeMetadata<NodeMetadata>> types, Set<Label> discriminators) {
        Node node = graphDatabaseService.createNode(discriminators.toArray(new Label[discriminators.size()]));
        labelCache.put(node.getId(), discriminators);
        return node;
    }

    @Override
    public void deleteEntity(Node entity) {
        entity.delete();
        labelCache.remove(entity.getId());
    }

    @Override
    public ResultIterator<Node> findEntity(EntityTypeMetadata<NodeMetadata> entityTypeMetadata, Label discriminator, Map<PrimitivePropertyMethodMetadata<PropertyMetadata>, Object> values) {
        if (values.size() > 1) {
            throw new XOException("Only one property value is supported for find operation");
        }
        Map.Entry<PrimitivePropertyMethodMetadata<PropertyMetadata>, Object> entry = values.entrySet().iterator().next();
        PrimitivePropertyMethodMetadata<PropertyMetadata> propertyMethodMetadata = entry.getKey();
        if (propertyMethodMetadata == null) {
            IndexedPropertyMethodMetadata<?> indexedProperty = entityTypeMetadata.getDatastoreMetadata().getIndexedProperty();
            if (indexedProperty == null) {
                throw new XOException("Type " + entityTypeMetadata.getAnnotatedType().getAnnotatedElement().getName() + " has no indexed property.");
            }
            propertyMethodMetadata = indexedProperty.getPropertyMethodMetadata();
        }
        PropertyMetadata propertyMetadata = propertyMethodMetadata.getDatastoreMetadata();
        Object value = entry.getValue();
        ResourceIterable<Node> nodesByLabelAndProperty = graphDatabaseService.findNodesByLabelAndProperty(discriminator,
                propertyMetadata.getName(), value);
        final ResourceIterator<Node> iterator = nodesByLabelAndProperty.iterator();
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
    public void migrateEntity(Node entity, TypeMetadataSet<EntityTypeMetadata<NodeMetadata>> types, Set<Label> discriminators,
                              TypeMetadataSet<EntityTypeMetadata<NodeMetadata>> targetTypes, Set<Label> targetDiscriminators) {
        Set<Label> labelsToRemove = new HashSet<>(discriminators);
        labelsToRemove.removeAll(targetDiscriminators);
        for (Label label : labelsToRemove) {
            entity.removeLabel(label);
        }
        Set<Label> labelsToAdd = new HashSet<>(targetDiscriminators);
        labelsToAdd.removeAll(discriminators);
        for (Label label : labelsToAdd) {
            entity.addLabel(label);
        }
        labelCache.put(entity.getId(), targetDiscriminators);
    }

    @Override
    public void flushEntity(Node node) {
        labelCache.remove(node.getId());
    }


}
