package com.buschmais.xo.neo4j.impl.datastore;

import com.buschmais.xo.api.ResultIterator;
import com.buschmais.xo.api.XOException;
import com.buschmais.xo.neo4j.api.Neo4jDatastoreSession;
import com.buschmais.xo.neo4j.api.annotation.Cypher;
import com.buschmais.xo.neo4j.impl.datastore.metadata.Neo4jRelationshipType;
import com.buschmais.xo.neo4j.impl.datastore.metadata.NodeMetadata;
import com.buschmais.xo.neo4j.impl.datastore.metadata.PropertyMetadata;
import com.buschmais.xo.spi.datastore.DatastorePropertyManager;
import com.buschmais.xo.spi.datastore.DatastoreQuery;
import com.buschmais.xo.spi.datastore.TypeMetadataSet;
import com.buschmais.xo.spi.metadata.method.IndexedPropertyMethodMetadata;
import com.buschmais.xo.spi.metadata.method.PrimitivePropertyMethodMetadata;
import com.buschmais.xo.spi.metadata.type.EntityTypeMetadata;
import org.neo4j.graphdb.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Abstract base implementation of a Neo4j database session based on the
 * {@link org.neo4j.graphdb.GraphDatabaseService} API.
 * 
 * @param <GDS>
 *            The type of {@link org.neo4j.graphdb.GraphDatabaseService}.
 */
public abstract class AbstractNeo4jDatastoreSession<GDS extends GraphDatabaseService> implements Neo4jDatastoreSession<GDS> {

    private final GDS graphDatabaseService;
    private final Neo4jPropertyManager propertyManager;

    private final Map<Long, Set<Label>> labelCache = new HashMap<>();

    public AbstractNeo4jDatastoreSession(GDS graphDatabaseService) {
        this.graphDatabaseService = graphDatabaseService;
        this.propertyManager = new Neo4jPropertyManager();
    }

    @Override
    public DatastorePropertyManager getDatastorePropertyManager() {
        return propertyManager;
    }

    @Override
    public GDS getGraphDatabaseService() {
        return graphDatabaseService;
    }

    @Override
    public Node createEntity(TypeMetadataSet<EntityTypeMetadata<NodeMetadata>> types, Set<Label> discriminators) {
        Node node = getGraphDatabaseService().createNode(discriminators.toArray(new Label[discriminators.size()]));
        labelCache.put(node.getId(), discriminators);
        return node;
    }

    @Override
    public ResultIterator<Node> findEntity(EntityTypeMetadata<NodeMetadata> entityTypeMetadata, Label discriminator, Object value) {
        IndexedPropertyMethodMetadata<?> indexedProperty = entityTypeMetadata.getDatastoreMetadata().getIndexedProperty();
        if (indexedProperty == null) {
            indexedProperty = entityTypeMetadata.getIndexedProperty();
        }
        if (indexedProperty == null) {
            throw new XOException("Type " + entityTypeMetadata.getAnnotatedType().getAnnotatedElement().getName() + " has no indexed property.");
        }
        PrimitivePropertyMethodMetadata<PropertyMetadata> propertyMethodMetadata = indexedProperty.getPropertyMethodMetadata();
        ResourceIterable<Node> nodesByLabelAndProperty = getGraphDatabaseService().findNodesByLabelAndProperty(discriminator,
                propertyMethodMetadata.getDatastoreMetadata().getName(), value);
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
    public boolean isEntity(Object o) {
        return Node.class.isAssignableFrom(o.getClass());
    }

    @Override
    public boolean isRelation(Object o) {
        return Relationship.class.isAssignableFrom(o.getClass());
    }

    @Override
    public Long getEntityId(Node entity) {
        return Long.valueOf(entity.getId());
    }

    @Override
    public void deleteEntity(Node entity) {
        entity.delete();
        labelCache.remove(entity.getId());
    }

    @Override
    public void flushEntity(Node node) {
        labelCache.remove(node.getId());
    }

    protected <QL> String getCypher(QL expression) {
        if (expression instanceof String) {
            return (String) expression;
        } else if (expression instanceof AnnotatedElement) {
            AnnotatedElement typeExpression = (AnnotatedElement) expression;
            Cypher cypher = typeExpression.getAnnotation(Cypher.class);
            if (cypher == null) {
                throw new XOException(typeExpression + " must be annotated with " + Cypher.class.getName());
            }
            return cypher.value();
        }
        throw new XOException("Unsupported query expression " + expression);
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
    public Long getRelationId(Relationship relationship) {
        return relationship.getId();
    }

    @Override
    public Neo4jRelationshipType getRelationDiscriminator(Relationship relationship) {
        return new Neo4jRelationshipType(relationship.getType());
    }

    @Override
    public void flushRelation(Relationship relationship) {
    }

    @Override
    public void close() {
    }

    @Override
    public Class<? extends Annotation> getDefaultQueryLanguage() {
        return Cypher.class;
    }
}
