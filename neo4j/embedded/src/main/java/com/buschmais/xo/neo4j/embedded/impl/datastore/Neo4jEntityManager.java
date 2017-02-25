package com.buschmais.xo.neo4j.embedded.impl.datastore;

import static com.buschmais.xo.neo4j.spi.helper.MetadataHelper.getIndexedPropertyMetadata;

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
import com.buschmais.xo.spi.metadata.method.PrimitivePropertyMethodMetadata;
import com.buschmais.xo.spi.metadata.type.EntityTypeMetadata;

/**
 * Implementation of a
 * {@link com.buschmais.xo.spi.datastore.DatastoreEntityManager} for Neo4j.
 */
public class Neo4jEntityManager extends AbstractNeo4jPropertyManager<EmbeddedNode>
        implements DatastoreEntityManager<Long, EmbeddedNode, NodeMetadata<EmbeddedLabel>, EmbeddedLabel, PropertyMetadata> {

    private final GraphDatabaseService graphDatabaseService;

    public Neo4jEntityManager(GraphDatabaseService graphDatabaseService) {
        this.graphDatabaseService = graphDatabaseService;
    }

    @Override
    public boolean isEntity(Object o) {
        return EmbeddedNode.class.isAssignableFrom(o.getClass());
    }

    @Override
    public Set<EmbeddedLabel> getEntityDiscriminators(EmbeddedNode node) {
        return node.getLabels();
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
        return node;
    }

    @Override
    public void deleteEntity(EmbeddedNode entity) {
        entity.delete();
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
        Object value = entry.getValue();
        PropertyMetadata propertyMetadata = getIndexedPropertyMetadata(entityTypeMetadata, entry.getKey());
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
    public void addDiscriminators(TypeMetadataSet<EntityTypeMetadata<NodeMetadata<EmbeddedLabel>>> types, EmbeddedNode node, Set<EmbeddedLabel> labels) {
        for (EmbeddedLabel label : labels) {
            node.addLabel(label);
        }
    }

    @Override
    public void removeDiscriminators(TypeMetadataSet<EntityTypeMetadata<NodeMetadata<EmbeddedLabel>>> removedTypes, EmbeddedNode node, Set<EmbeddedLabel> labels) {
        for (EmbeddedLabel label : labels) {
            node.removeLabel(label);
        }
    }

    @Override
    public void clear(Iterable<EmbeddedNode> nodes) {
        for (EmbeddedNode node : nodes) {
            node.clear();
        }
    }

    @Override
    public void flush(Iterable<EmbeddedNode> nodes) {
        for (EmbeddedNode node : nodes) {
            node.flush();
        }
    }
}
