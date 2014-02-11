package com.buschmais.cdo.neo4j.test.rest;

import com.buschmais.cdo.api.ConcurrencyMode;
import com.buschmais.cdo.api.ValidationMode;
import com.buschmais.cdo.api.bootstrap.CdoUnit;
import com.buschmais.cdo.neo4j.api.Neo4jCdoProvider;
import com.buschmais.cdo.neo4j.test.AbstractCdoManagerTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.kernel.GraphDatabaseAPI;
import org.neo4j.server.WrappingNeoServer;
import org.neo4j.test.TestGraphDatabaseFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Properties;

public abstract class AbstractRestCdoManagerTest extends AbstractCdoManagerTest {

    private static WrappingNeoServer server;

    @Override
    protected CdoUnit getCdoUnit(Class<?>[] types) throws URISyntaxException {
        return new CdoUnit("rest", "REST CDO unit", new URI("http://localhost:7474/db/data"),
                Neo4jCdoProvider.class, types, ValidationMode.AUTO, ConcurrencyMode.SINGLETHREADED, getTransactionAttribute(), new Properties(), Collections.<Class<?>>emptyList());
    }

    @BeforeClass
    public static void startServer() {
        GraphDatabaseService graphDatabaseService = new TestGraphDatabaseFactory().newImpermanentDatabase();
        server = new WrappingNeoServer((GraphDatabaseAPI) graphDatabaseService);
        server.start();
    }

    @AfterClass
    public static void stopServer() {
        server.stop();
    }

}
