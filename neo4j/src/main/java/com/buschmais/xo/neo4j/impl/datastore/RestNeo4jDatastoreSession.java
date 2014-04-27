package com.buschmais.xo.neo4j.impl.datastore;

import java.util.HashMap;
import java.util.Map;

import org.neo4j.rest.graphdb.RestGraphDatabase;
import org.neo4j.rest.graphdb.entity.RestEntity;

import com.buschmais.xo.api.NativeQuery;
import com.buschmais.xo.api.NativeQueryEngine;
import com.buschmais.xo.api.ResultIterator;
import com.buschmais.xo.neo4j.impl.datastore.query.RestCypherQueryEngine;
import com.buschmais.xo.spi.datastore.DatastoreTransaction;

public class RestNeo4jDatastoreSession extends AbstractNeo4jDatastoreSession<RestGraphDatabase> {

    private class RestNeo4jDatastoreTransaction implements DatastoreTransaction {

        @Override
        public void begin() {
        }

        @Override
        public void commit() {
        }

        @Override
        public void rollback() {
        }

        @Override
        public boolean isActive() {
            return true;
        }
    }

    private final NativeQueryEngine<?> cypherQueryEngine;

    private final DatastoreTransaction transaction;

    public RestNeo4jDatastoreSession(final RestGraphDatabase graphDatabaseService) {
        super(graphDatabaseService);
        transaction = new RestNeo4jDatastoreTransaction();

        // TODO: dynamically register native query engines - plugins?
        cypherQueryEngine = new RestCypherQueryEngine(graphDatabaseService);
    }

    @Override
    public DatastoreTransaction getDatastoreTransaction() {
        return transaction;
    }

    @Override
    public ResultIterator<Map<String, Object>> executeQuery(final NativeQuery query, final Map<String, Object> parameters) {
        final Map<String, Object> effectiveParameters = translateParameters(parameters);
        final NativeQueryEngine engine = getNativeQueryEngine(query);
        return engine.execute(query, effectiveParameters);
    }

    @Override
    public NativeQueryEngine getNativeQueryEngine(final NativeQuery<?> query) {
        return cypherQueryEngine;
    }

    private Map<String, Object> translateParameters(final Map<String, Object> parameters) {
        final Map<String, Object> effectiveParameters = new HashMap<>();
        for (final Map.Entry<String, Object> parameterEntry : parameters.entrySet()) {
            Object value = parameterEntry.getValue();
            if (value instanceof RestEntity) {
                value = ((RestEntity) value).getId();
            }
            effectiveParameters.put(parameterEntry.getKey(), value);
        }
        return effectiveParameters;
    }

}
