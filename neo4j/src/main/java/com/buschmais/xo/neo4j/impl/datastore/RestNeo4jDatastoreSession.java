package com.buschmais.xo.neo4j.impl.datastore;

import com.buschmais.xo.api.ResultIterator;
import com.buschmais.xo.spi.datastore.DatastoreTransaction;
import org.neo4j.rest.graphdb.RestAPI;
import org.neo4j.rest.graphdb.RestGraphDatabase;
import org.neo4j.rest.graphdb.query.RestCypherQueryEngine;
import org.neo4j.rest.graphdb.util.QueryResult;

import java.util.Iterator;
import java.util.Map;

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

    private final DatastoreTransaction transaction;

    public RestNeo4jDatastoreSession(RestGraphDatabase graphDatabaseService) {
        super(graphDatabaseService);
        transaction = new RestNeo4jDatastoreTransaction();
    }

    @Override
    public DatastoreTransaction getDatastoreTransaction() {
        return transaction;
    }

    @Override
    public <QL> ResultIterator<Map<String, Object>> executeQuery(QL expression, Map<String, Object> parameters) {
        RestAPI restAPI = getGraphDatabaseService().getRestAPI();
        RestCypherQueryEngine restCypherQueryEngine = new RestCypherQueryEngine(restAPI);
        QueryResult<Map<String, Object>> queryResult = restCypherQueryEngine.query(getCypher(expression), parameters);
        final Iterator<Map<String, Object>> iterator = queryResult.iterator();
        return new ResultIterator<Map<String, Object>>() {

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public Map<String, Object> next() {
                return iterator.next();
            }

            @Override
            public void remove() {
                iterator.remove();
            }

            @Override
            public void close() {
            }
        };
    }

}
