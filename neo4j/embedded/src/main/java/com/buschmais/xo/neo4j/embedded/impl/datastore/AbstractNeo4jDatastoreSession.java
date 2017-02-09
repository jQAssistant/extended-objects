package com.buschmais.xo.neo4j.embedded.impl.datastore;

import java.lang.annotation.Annotation;
import java.util.*;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.neo4j.api.TypedNeo4jRepository;
import com.buschmais.xo.neo4j.api.annotation.Cypher;
import com.buschmais.xo.neo4j.embedded.api.Neo4jDatastoreSession;
import com.buschmais.xo.neo4j.embedded.impl.model.EmbeddedLabel;
import com.buschmais.xo.neo4j.embedded.impl.model.EmbeddedNode;
import com.buschmais.xo.neo4j.embedded.impl.model.EmbeddedRelationship;
import com.buschmais.xo.neo4j.embedded.impl.model.EmbeddedRelationshipType;
import com.buschmais.xo.neo4j.spi.metadata.NodeMetadata;
import com.buschmais.xo.neo4j.spi.metadata.PropertyMetadata;
import com.buschmais.xo.neo4j.spi.metadata.RelationshipMetadata;
import com.buschmais.xo.spi.datastore.DatastoreEntityManager;
import com.buschmais.xo.spi.datastore.DatastoreRelationManager;
import com.buschmais.xo.spi.reflection.ClassHelper;
import com.buschmais.xo.spi.session.XOSession;

/**
 * Abstract base implementation of a Neo4j database session based on the
 * {@link org.neo4j.graphdb.GraphDatabaseService} API.
 *
 * @param <GDS>
 *            The type of {@link org.neo4j.graphdb.GraphDatabaseService}.
 */
public abstract class AbstractNeo4jDatastoreSession<GDS extends GraphDatabaseService> implements Neo4jDatastoreSession<GDS> {

    private final GDS graphDatabaseService;
    private final Neo4jEntityManager entityManager;
    private final Neo4jRelationManager relationManager;

    public AbstractNeo4jDatastoreSession(GDS graphDatabaseService) {
        this.graphDatabaseService = graphDatabaseService;
        this.entityManager = new Neo4jEntityManager(graphDatabaseService);
        this.relationManager = new Neo4jRelationManager(graphDatabaseService);
    }

    @Override
    public DatastoreEntityManager<Long, EmbeddedNode, NodeMetadata<EmbeddedLabel>, EmbeddedLabel, PropertyMetadata> getDatastoreEntityManager() {
        return entityManager;
    }

    @Override
    public DatastoreRelationManager<EmbeddedNode, Long, EmbeddedRelationship, RelationshipMetadata<EmbeddedRelationshipType>, EmbeddedRelationshipType, PropertyMetadata> getDatastoreRelationManager() {
        return relationManager;
    }

    @Override
    public Class<? extends Annotation> getDefaultQueryLanguage() {
        return Cypher.class;
    }

    @Override
    public GDS getGraphDatabaseService() {
        return graphDatabaseService;
    }

    @Override
    public <R> R createRepository(XOSession xoSession, Class<R> type) {
        if (TypedNeo4jRepository.class.isAssignableFrom(type)) {
            Class<?> typeParameter = ClassHelper.getTypeParameter(TypedNeo4jRepository.class, type);
            if (typeParameter == null) {
                throw new XOException("Cannot determine type parameter for " + type.getName());
            }
            return (R) new TypedNeoj4RepositoryImpl<>(typeParameter, graphDatabaseService, xoSession);
        }
        return (R) new Neo4jRepositoryImpl(graphDatabaseService, xoSession);
    }

    @Override
    public void close() {
    }

    @Override
    public Object convertValue(Object value) {
        if (value instanceof Node) {
            return new EmbeddedNode((Node) value);
        } else if (value instanceof Relationship) {
            return new EmbeddedRelationship((Relationship) value);
        } else if (value instanceof Iterable<?>) {
            Iterable<?> iterable = (Iterable<?>) value;
            List<Object> values = new ArrayList<>();
            for (Object o : iterable) {
                values.add(convertValue(o));
            }
            return values;
        } else if (value instanceof Map<?, ?>) {
            Map<Object, Object> result = new HashMap<>();
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) value).entrySet()) {
                result.put(convertValue(entry.getKey()), convertValue(entry.getValue()));
            }
            return result;
        }
        return value;
    }

    @Override
    public Object convertParameter(Object value) {
        if (value instanceof EmbeddedNode) {
            return ((EmbeddedNode) value).getId();
        } else if (value instanceof EmbeddedRelationship) {
            return ((EmbeddedRelationship) value).getId();
        } else if (value instanceof Collection) {
            Collection collection = (Collection) value;
            List<Object> values = new ArrayList<>();
            for (Object o : collection) {
                values.add(convertParameter(o));
            }
            return values;
        }
        return value;
    }
}
