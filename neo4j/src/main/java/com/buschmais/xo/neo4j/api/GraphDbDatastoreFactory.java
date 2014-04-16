package com.buschmais.xo.neo4j.api;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.neo4j.impl.datastore.EmbeddedNeo4jDatastore;
import org.neo4j.graphdb.GraphDatabaseService;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.Properties;

/**
 * A {@link DatastoreFactory} re-using an existing instance of a {@link GraphDatabaseService} which must be specified as a property of the {@link com.buschmais.xo.api.bootstrap.XOUnit}.
 */
public class GraphDbDatastoreFactory implements DatastoreFactory<EmbeddedNeo4jDatastore> {

    @Override
    public EmbeddedNeo4jDatastore createGraphDatabaseService(URI uri, Properties properties) throws MalformedURLException {
        String graphDbPropertyName = GraphDatabaseService.class.getName();
        GraphDatabaseService graphDatabaseService = (GraphDatabaseService) properties.get(graphDbPropertyName);
        if (graphDatabaseService == null) {
            throw new XOException("Property " + graphDbPropertyName + " is not specified.");
        }
        return new EmbeddedNeo4jDatastore(graphDatabaseService);
    }
}
