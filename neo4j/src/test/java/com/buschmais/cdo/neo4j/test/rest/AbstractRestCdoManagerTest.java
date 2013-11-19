package com.buschmais.cdo.neo4j.test.rest;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.api.CdoManagerFactory;
import com.buschmais.cdo.neo4j.impl.AbstractNeo4jCdoManagerFactoryImpl;
import com.buschmais.cdo.neo4j.impl.EmbeddedNeo4jCdoManagerFactoryImpl;
import com.buschmais.cdo.neo4j.impl.RestNeo4jCdoManagerFactoryImpl;
import com.buschmais.cdo.neo4j.test.AbstractCdoManagerTest;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.kernel.GraphDatabaseAPI;
import org.neo4j.server.WrappingNeoServer;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import static com.buschmais.cdo.api.Query.Result;
import static com.buschmais.cdo.api.Query.Result.CompositeRowObject;

public abstract class AbstractRestCdoManagerTest extends AbstractCdoManagerTest {

    private static WrappingNeoServer server;

    @Override
    protected AbstractNeo4jCdoManagerFactoryImpl getNeo4jCdoManagerFactory(Class<?>[] types) throws MalformedURLException {
        return new RestNeo4jCdoManagerFactoryImpl(new URL("http://localhost:7474/db/data"), types);
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
