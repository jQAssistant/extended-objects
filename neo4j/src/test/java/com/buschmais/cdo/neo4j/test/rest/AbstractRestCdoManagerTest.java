package com.buschmais.cdo.neo4j.test.rest;

import com.buschmais.cdo.neo4j.impl.AbstractCdoManagerFactoryImpl;
import com.buschmais.cdo.neo4j.impl.RestNeo4jCdoManagerFactoryImpl;
import com.buschmais.cdo.neo4j.test.AbstractCdoManagerTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.kernel.GraphDatabaseAPI;
import org.neo4j.server.WrappingNeoServer;

import java.net.MalformedURLException;

public abstract class AbstractRestCdoManagerTest extends AbstractCdoManagerTest {

    private static WrappingNeoServer server;

    @Override
    protected AbstractCdoManagerFactoryImpl getNeo4jCdoManagerFactory(Class<?>[] types) throws MalformedURLException {
        return new RestNeo4jCdoManagerFactoryImpl(createCdoUnit("http://localhost:7474/db/data", types));
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
