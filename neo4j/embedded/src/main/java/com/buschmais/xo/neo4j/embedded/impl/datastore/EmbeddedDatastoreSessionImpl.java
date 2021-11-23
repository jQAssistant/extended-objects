package com.buschmais.xo.neo4j.embedded.impl.datastore;

import static java.util.Collections.singletonList;

import java.lang.annotation.Annotation;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.neo4j.api.TypedNeo4jRepository;
import com.buschmais.xo.neo4j.api.annotation.Cypher;
import com.buschmais.xo.neo4j.embedded.api.EmbeddedNeo4jDatastoreSession;
import com.buschmais.xo.neo4j.embedded.impl.converter.EmbeddedParameterConverter;
import com.buschmais.xo.neo4j.embedded.impl.converter.EmbeddedValueConverter;
import com.buschmais.xo.neo4j.embedded.impl.model.EmbeddedLabel;
import com.buschmais.xo.neo4j.embedded.impl.model.EmbeddedNode;
import com.buschmais.xo.neo4j.embedded.impl.model.EmbeddedRelationship;
import com.buschmais.xo.neo4j.embedded.impl.model.EmbeddedRelationshipType;
import com.buschmais.xo.neo4j.spi.AbstractNeo4jDatastoreSession;
import com.buschmais.xo.neo4j.spi.helper.Converter;
import com.buschmais.xo.neo4j.spi.metadata.NodeMetadata;
import com.buschmais.xo.neo4j.spi.metadata.PropertyMetadata;
import com.buschmais.xo.neo4j.spi.metadata.RelationshipMetadata;
import com.buschmais.xo.spi.datastore.DatastoreEntityManager;
import com.buschmais.xo.spi.datastore.DatastoreQuery;
import com.buschmais.xo.spi.datastore.DatastoreRelationManager;
import com.buschmais.xo.spi.reflection.ClassHelper;
import com.buschmais.xo.spi.session.XOSession;

import org.neo4j.graphdb.GraphDatabaseService;

public class EmbeddedDatastoreSessionImpl extends
        AbstractNeo4jDatastoreSession<EmbeddedNode, EmbeddedLabel, EmbeddedRelationship, EmbeddedRelationshipType> implements EmbeddedNeo4jDatastoreSession {

    private final GraphDatabaseService graphDatabaseService;
    private final EmbeddedNeo4jDatastoreTransaction datastoreTransaction;
    private final EmbeddedEntityManager entityManager;
    private final EmbeddedRelationManager relationManager;
    private final Converter parameterConverter;
    private final Converter valueConverter;

    public EmbeddedDatastoreSessionImpl(EmbeddedDatastoreTransaction transaction, GraphDatabaseService graphDatabaseService) {
        this.graphDatabaseService = graphDatabaseService;
        this.entityManager = new EmbeddedRepository(transaction);
        this.relationManager = new EmbeddedRelationManager(transaction);
        this.parameterConverter = new Converter(singletonList(new EmbeddedParameterConverter()));
        this.valueConverter = new Converter(singletonList(new EmbeddedValueConverter(transaction)));
        this.datastoreTransaction = transaction;
    }

    @Override
    public EmbeddedNeo4jDatastoreTransaction getDatastoreTransaction() {
        return datastoreTransaction;
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

    public GraphDatabaseService getGraphDatabaseService() {
        return graphDatabaseService;
    }

    @Override
    public <QL extends Annotation> DatastoreQuery<QL> createQuery(Class<QL> queryLanguage) {
        if (Cypher.class.equals(queryLanguage)) {
            return (DatastoreQuery<QL>) new EmbeddedCypherQuery(this);
        }
        throw new XOException("Unsupported query language: " + queryLanguage.getName());
    }

    @Override
    public <R> R createRepository(XOSession xoSession, Class<R> type) {
        if (TypedNeo4jRepository.class.isAssignableFrom(type)) {
            Class<?> typeParameter = ClassHelper.getTypeParameter(TypedNeo4jRepository.class, type);
            if (typeParameter == null) {
                throw new XOException("Cannot determine type parameter for " + type.getName());
            }
            return (R) new TypedEmbeddedRepository<>(typeParameter, datastoreTransaction, xoSession);
        }
        return (R) new EmbeddedRepository(datastoreTransaction, xoSession);
    }

    @Override
    public void close() {
        // forced by interface
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
