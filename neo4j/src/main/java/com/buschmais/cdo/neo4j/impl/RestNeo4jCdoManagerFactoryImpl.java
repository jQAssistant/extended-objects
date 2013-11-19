package com.buschmais.cdo.neo4j.impl;

import com.buschmais.cdo.neo4j.impl.node.metadata.NodeMetadataProvider;
import com.buschmais.cdo.neo4j.impl.query.QueryExecutor;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.rest.graphdb.RestAPI;
import org.neo4j.rest.graphdb.RestGraphDatabase;
import org.neo4j.rest.graphdb.query.RestCypherQueryEngine;
import org.neo4j.rest.graphdb.util.QueryResult;

import java.net.URL;
import java.util.Iterator;
import java.util.Map;

public class RestNeo4jCdoManagerFactoryImpl extends AbstractNeo4jCdoManagerFactoryImpl<RestGraphDatabase> {

    public RestNeo4jCdoManagerFactoryImpl(URL url, Class<?>... entities) {
        super(url, entities);
    }

    protected RestGraphDatabase createGraphDatabaseService(URL url) {
        RestGraphDatabase restGraphDatabase = new RestGraphDatabase(url.toExternalForm());
        return restGraphDatabase;
    }

    @Override
    protected QueryExecutor createQueryExecutor(RestGraphDatabase graphDatabase) {
        RestAPI restAPI = graphDatabase.getRestAPI();
        final RestCypherQueryEngine restCypherQueryEngine = new RestCypherQueryEngine(restAPI);
        return new QueryExecutor() {
            @Override
            public Iterator<Map<String, Object>> execute(String query, Map<String, Object> parameters) {
                QueryResult<Map<String, Object>> queryResult = restCypherQueryEngine.query(query, parameters);
                return queryResult.iterator();
            }
        };
    }

    @Override
    protected void init(GraphDatabaseService graphDatabaseService, NodeMetadataProvider nodeMetadataProvider) {
    }
}
