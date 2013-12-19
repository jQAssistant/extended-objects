package com.buschmais.cdo.neo4j.api;

import com.buschmais.cdo.neo4j.impl.datastore.EmbeddedNeo4jDatastore;
import com.buschmais.cdo.neo4j.impl.datastore.RestNeo4jDatastore;
import com.buschmais.cdo.spi.bootstrap.CdoDatastoreProvider;
import com.buschmais.cdo.api.bootstrap.CdoUnit;
import com.buschmais.cdo.spi.datastore.Datastore;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import java.net.URL;

public class Neo4jCdoProvider implements CdoDatastoreProvider {

    @Override
    public Datastore<?, ?, ?> createDatastore(CdoUnit cdoUnit) {
        URL url = cdoUnit.getUrl();
        String protocol = url.getProtocol().toLowerCase();
        if ("file".equals(protocol)) {
            GraphDatabaseService graphDatabaseService = new GraphDatabaseFactory().newEmbeddedDatabase(url.getPath());
            return new EmbeddedNeo4jDatastore(graphDatabaseService);
        } else if ("http".equals(protocol) || "https".equals(protocol)) {
            return new RestNeo4jDatastore(url.toExternalForm());
        }
        return null;
    }
}
