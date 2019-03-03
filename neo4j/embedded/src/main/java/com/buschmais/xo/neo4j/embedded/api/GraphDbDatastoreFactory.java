package com.buschmais.xo.neo4j.embedded.api;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.Properties;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.neo4j.embedded.impl.datastore.GraphDbNeo4jDatastore;

import org.neo4j.graphdb.GraphDatabaseService;

/**
 * A {@link DatastoreFactory} re-using an existing instance of a
 * {@link GraphDatabaseService} which must be specified as a property of the
 * {@link com.buschmais.xo.api.bootstrap.XOUnit}.
 */
public class GraphDbDatastoreFactory implements DatastoreFactory<GraphDbNeo4jDatastore> {

    @Override
    public GraphDbNeo4jDatastore createGraphDatabaseService(URI uri, Properties properties) throws MalformedURLException {
        String graphDbPropertyName = GraphDatabaseService.class.getName();
        GraphDatabaseService graphDatabaseService = (GraphDatabaseService) properties.get(graphDbPropertyName);
        if (graphDatabaseService == null) {
            throw new XOException("Property " + graphDbPropertyName + " is not specified.");
        }
        return new GraphDbNeo4jDatastore(graphDatabaseService);
    }
}
