package com.buschmais.cdo.neo4j.impl.datastore;

import com.buschmais.cdo.neo4j.impl.node.metadata.NodeMetadataProvider;
import org.neo4j.rest.graphdb.RestAPI;
import org.neo4j.rest.graphdb.RestGraphDatabase;
import org.neo4j.rest.graphdb.query.RestCypherQueryEngine;
import org.neo4j.rest.graphdb.util.QueryResult;

import java.util.Iterator;
import java.util.Map;

public class RestNeo4jDatastoreSession extends AbstractNeo4jDatastoreSession<RestGraphDatabase> {

    public RestNeo4jDatastoreSession(RestGraphDatabase graphDatabaseService, NodeMetadataProvider metadataProvider) {
        super(graphDatabaseService, metadataProvider);
    }

    @Override
    public Iterator<Map<String, Object>> execute(String query, Map<String, Object> parameters) {
        RestAPI restAPI = getGraphDatabaseService().getRestAPI();
        RestCypherQueryEngine restCypherQueryEngine = new RestCypherQueryEngine(restAPI);
        QueryResult<Map<String, Object>> queryResult = restCypherQueryEngine.query(query, parameters);
        return queryResult.iterator();
    }

    @Override
    public void begin() {
    }

    @Override
    public void commit() {
    }

    @Override
    public void rollback() {
    }

}
