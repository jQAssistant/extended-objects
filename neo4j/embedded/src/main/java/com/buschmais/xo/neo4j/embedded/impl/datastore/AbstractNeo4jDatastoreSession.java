package com.buschmais.xo.neo4j.embedded.impl.datastore;

import java.lang.annotation.Annotation;
import java.util.Arrays;

import org.neo4j.graphdb.GraphDatabaseService;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.neo4j.api.TypedNeo4jRepository;
import com.buschmais.xo.neo4j.api.annotation.Cypher;
import com.buschmais.xo.neo4j.embedded.api.Neo4jDatastoreSession;
import com.buschmais.xo.neo4j.embedded.impl.converter.EmbeddedParameterConverter;
import com.buschmais.xo.neo4j.embedded.impl.converter.EmbeddedValueConverter;
import com.buschmais.xo.neo4j.embedded.impl.model.EmbeddedLabel;
import com.buschmais.xo.neo4j.embedded.impl.model.EmbeddedNode;
import com.buschmais.xo.neo4j.embedded.impl.model.EmbeddedRelationship;
import com.buschmais.xo.neo4j.embedded.impl.model.EmbeddedRelationshipType;
import com.buschmais.xo.neo4j.spi.helper.Converter;
import com.buschmais.xo.neo4j.spi.metadata.NodeMetadata;
import com.buschmais.xo.neo4j.spi.metadata.PropertyMetadata;
import com.buschmais.xo.neo4j.spi.metadata.RelationshipMetadata;
import com.buschmais.xo.spi.datastore.DatastoreEntityManager;
import com.buschmais.xo.spi.datastore.DatastoreRelationManager;
import com.buschmais.xo.spi.reflection.ClassHelper;
import com.buschmais.xo.spi.session.XOSession;

/**
 * Abstract base implementation of a Neo4j database session based on the
 * {@link GraphDatabaseService} API.
 *
 * @param <GDS>
 *            The type of {@link GraphDatabaseService}.
 */
public abstract class AbstractNeo4jDatastoreSession<GDS extends GraphDatabaseService> implements Neo4jDatastoreSession<GDS> {

    private final GDS graphDatabaseService;
    private final Neo4jEntityManager entityManager;
    private final Neo4jRelationManager relationManager;
    private final Converter parameterConverter;
    private final Converter valueConverter;

    public AbstractNeo4jDatastoreSession(GDS graphDatabaseService) {
        this.graphDatabaseService = graphDatabaseService;
        this.entityManager = new Neo4jEntityManager(graphDatabaseService);
        this.relationManager = new Neo4jRelationManager(graphDatabaseService);
        this.parameterConverter = new Converter(Arrays.asList(new EmbeddedParameterConverter()));
        this.valueConverter = new Converter(Arrays.asList(new EmbeddedValueConverter()));
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
    public Object convertParameter(Object value) {
        return parameterConverter.convert(value);
    }

    @Override
    public Object convertValue(Object value) {
        return valueConverter.convert(value);
    }
}
