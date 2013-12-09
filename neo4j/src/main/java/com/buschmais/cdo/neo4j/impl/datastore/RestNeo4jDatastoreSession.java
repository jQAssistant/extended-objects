package com.buschmais.cdo.neo4j.impl.datastore;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.api.ResultIterator;
import com.buschmais.cdo.neo4j.impl.node.metadata.MetadataProvider;
import org.neo4j.rest.graphdb.RestAPI;
import org.neo4j.rest.graphdb.RestGraphDatabase;
import org.neo4j.rest.graphdb.query.RestCypherQueryEngine;
import org.neo4j.rest.graphdb.util.QueryResult;

import java.util.Iterator;
import java.util.Map;

public class RestNeo4jDatastoreSession extends AbstractNeo4jDatastoreSession<RestGraphDatabase> {

    public RestNeo4jDatastoreSession(RestGraphDatabase graphDatabaseService, MetadataProvider metadataProvider) {
        super(graphDatabaseService, metadataProvider);
    }

    @Override
    public DatastoreTransaction getDatastoreTransaction() {
        throw new CdoException("Transactions are not supported for this datastore.");
    }

    @Override
    public <QL> ResultIterator<Map<String, Object>> execute(QL expression, Map<String, Object> parameters) {
        RestAPI restAPI = getGraphDatabaseService().getRestAPI();
        RestCypherQueryEngine restCypherQueryEngine = new RestCypherQueryEngine(restAPI);
        QueryResult<Map<String, Object>> queryResult = restCypherQueryEngine.query(getCypher(expression), parameters);
        final Iterator<Map<String, Object>> iterator = queryResult.iterator();
        return new ResultIterator() {

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public Object next() {
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
