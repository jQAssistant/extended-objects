package com.buschmais.xo.neo4j.impl.datastore;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ResourceIterable;
import org.neo4j.graphdb.ResourceIterator;

import com.buschmais.xo.api.NativeQuery;
import com.buschmais.xo.api.NativeQueryEngine;
import com.buschmais.xo.api.ResultIterator;
import com.buschmais.xo.api.XOException;
import com.buschmais.xo.neo4j.api.Neo4jDatastoreSession;
import com.buschmais.xo.neo4j.api.annotation.Cypher;
import com.buschmais.xo.neo4j.api.query.CypherQuery;
import com.buschmais.xo.neo4j.impl.datastore.metadata.Neo4jRelationshipType;
import com.buschmais.xo.neo4j.impl.datastore.metadata.NodeMetadata;
import com.buschmais.xo.neo4j.impl.datastore.metadata.PropertyMetadata;
import com.buschmais.xo.neo4j.impl.datastore.query.ResourceResultIterator;
import com.buschmais.xo.spi.datastore.DatastorePropertyManager;
import com.buschmais.xo.spi.datastore.TypeMetadataSet;
import com.buschmais.xo.spi.metadata.method.IndexedPropertyMethodMetadata;
import com.buschmais.xo.spi.metadata.method.PrimitivePropertyMethodMetadata;
import com.buschmais.xo.spi.metadata.type.EntityTypeMetadata;

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

    public AbstractNeo4jDatastoreSession(final GDS graphDatabaseService) {
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
    public Node createEntity(final TypeMetadataSet<EntityTypeMetadata<NodeMetadata>> types, final Set<Label> discriminators) {
        final Node node = getGraphDatabaseService().createNode(discriminators.toArray(new Label[discriminators.size()]));
        labelCache.put(node.getId(), discriminators);
        return node;
    }

    @Override
    public ResultIterator<Node> findEntity(final EntityTypeMetadata<NodeMetadata> entityTypeMetadata, final Label discriminator, final Object value) {
        IndexedPropertyMethodMetadata<?> indexedProperty = entityTypeMetadata.getDatastoreMetadata().getIndexedProperty();
        if (indexedProperty == null) {
            indexedProperty = entityTypeMetadata.getIndexedProperty();
        }
        if (indexedProperty == null) {
            throw new XOException("Type " + entityTypeMetadata.getAnnotatedType().getAnnotatedElement().getName() + " has no indexed property.");
        }
        final PrimitivePropertyMethodMetadata<PropertyMetadata> propertyMethodMetadata = indexedProperty.getPropertyMethodMetadata();
        final ResourceIterable<Node> nodesByLabelAndProperty = getGraphDatabaseService().findNodesByLabelAndProperty(discriminator,
                propertyMethodMetadata.getDatastoreMetadata().getName(), value);
        final ResourceIterator<Node> iterator = nodesByLabelAndProperty.iterator();
        return new ResourceResultIterator(iterator);
    }

    @Override
    public void migrateEntity(final Node entity, final TypeMetadataSet<EntityTypeMetadata<NodeMetadata>> types, final Set<Label> discriminators,
            final TypeMetadataSet<EntityTypeMetadata<NodeMetadata>> targetTypes, final Set<Label> targetDiscriminators) {
        final Set<Label> labelsToRemove = new HashSet<>(discriminators);
        labelsToRemove.removeAll(targetDiscriminators);
        for (final Label label : labelsToRemove) {
            entity.removeLabel(label);
        }
        final Set<Label> labelsToAdd = new HashSet<>(targetDiscriminators);
        labelsToAdd.removeAll(discriminators);
        for (final Label label : labelsToAdd) {
            entity.addLabel(label);
        }
        labelCache.put(entity.getId(), targetDiscriminators);
    }

    @Override
    public boolean isEntity(final Object o) {
        return Node.class.isAssignableFrom(o.getClass());
    }

    @Override
    public boolean isRelation(final Object o) {
        return Relationship.class.isAssignableFrom(o.getClass());
    }

    @Override
    public Long getEntityId(final Node entity) {
        return Long.valueOf(entity.getId());
    }

    @Override
    public void deleteEntity(final Node entity) {
        entity.delete();
        labelCache.remove(entity.getId());
    }

    @Override
    public void flushEntity(final Node node) {
        labelCache.remove(node.getId());
    }

    @Override
    public NativeQuery<?> getNativeQuery(final String expression, final Class<? extends Annotation> language) {
        return new CypherQuery(expression);
    }

    @Override
    public <QL> NativeQuery<?> getNativeQuery(final AnnotatedElement expression, final Class<? extends Annotation> language) {
        final Cypher cypher = expression.getAnnotation(Cypher.class);
        if (cypher != null) {
            return new CypherQuery(cypher.value());
        }
//        final Lucene lucene = expression.getAnnotation(Lucene.class);
//        if (lucene != null) {
//            return new LuceneQuery(lucene.value(), lucene.type());
//        }
        throw new XOException(expression + " must be annotated with one of supported native queries (" + Cypher.class.getName() + ")");
    }

    abstract NativeQueryEngine<?> getNativeQueryEngine(NativeQuery<?> query);

    @Override
    public Set<Label> getEntityDiscriminators(final Node node) {
        Set<Label> labels = labelCache.get(node.getId());
        if (labels == null) {
            labels = new HashSet<>();
            for (final Label label : node.getLabels()) {
                labels.add(label);
            }
            labelCache.put(node.getId(), labels);
        }
        return labels;
    }

    @Override
    public Long getRelationId(final Relationship relationship) {
        return relationship.getId();
    }

    @Override
    public Neo4jRelationshipType getRelationDiscriminator(final Relationship relationship) {
        return new Neo4jRelationshipType(relationship.getType());
    }

    @Override
    public void flushRelation(final Relationship relationship) {
    }

    @Override
    public void close() {
    }
}
