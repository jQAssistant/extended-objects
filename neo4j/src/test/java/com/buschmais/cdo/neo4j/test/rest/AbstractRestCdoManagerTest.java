package com.buschmais.cdo.neo4j.test.rest;

import com.buschmais.cdo.api.CdoManagerFactory;
import com.buschmais.cdo.api.ValidationMode;
import com.buschmais.cdo.api.bootstrap.Cdo;
import com.buschmais.cdo.neo4j.api.Neo4jCdoProvider;
import com.buschmais.cdo.neo4j.test.AbstractCdoManagerTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.kernel.GraphDatabaseAPI;
import org.neo4j.server.WrappingNeoServer;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

public abstract class AbstractRestCdoManagerTest extends AbstractCdoManagerTest {

    private static WrappingNeoServer server;

    @Override
    protected CdoManagerFactory getNeo4jCdoManagerFactory(Class<?>[] types) throws MalformedURLException {
        return Cdo.createCdoManagerFactory(new URL("http://localhost:7474/db/data"), Neo4jCdoProvider.class, types, ValidationMode.AUTO, getTransactionAttribute(), new Properties());
    }

    @BeforeClass
    public static void startServer() {
        GraphDatabaseService graphDatabaseService = new GraphDatabaseFactory().newEmbeddedDatabase("target/neo4j/server");
        server = new WrappingNeoServer((GraphDatabaseAPI) graphDatabaseService);
        server.start();
    }

    @AfterClass
    public static void stopServer() {
        server.stop();
    }

}
