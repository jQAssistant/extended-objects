package com.buschmais.xo.neo4j.test;

import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.kernel.GraphDatabaseDependencies;
import org.neo4j.kernel.configuration.Config;
import org.neo4j.kernel.impl.factory.GraphDatabaseFacade;
import org.neo4j.logging.FormattedLogProvider;
import org.neo4j.server.CommunityNeoServer;
import org.neo4j.test.TestGraphDatabaseFactory;

import com.buschmais.xo.api.bootstrap.XOUnit;

public abstract class AbstractNeo4jXOManagerServerTest extends AbstractNeo4jXOManagerTest {

    private static CommunityNeoServer server;

    protected AbstractNeo4jXOManagerServerTest(XOUnit xoUnit) {
        super(xoUnit);
    }

    @BeforeClass
    public static void startServer() {
        GraphDatabaseService databaseService = new TestGraphDatabaseFactory().newImpermanentDatabase();
        org.neo4j.server.database.Database.Factory factory = (config, dependencies) -> new org.neo4j.server.database.Database() {
            @Override
            public String getLocation() {
                return "mem";
            }

            @Override
            public GraphDatabaseFacade getGraph() {
                return (GraphDatabaseFacade) databaseService;
            }

            @Override
            public boolean isRunning() {
                return true;
            }

            @Override
            public void init() throws Throwable {

            }

            @Override
            public void start() throws Throwable {

            }

            @Override
            public void stop() throws Throwable {

            }

            @Override
            public void shutdown() throws Throwable {
                databaseService.shutdown();
            }
        };
        Map<String, String> opts = new HashMap<>();
        opts.put("dbms.connector.http.type", "HTTP");
        opts.put("dbms.connector.http.enabled", "true");
        Config defaults = new Config(opts); // Config.empty().with(opts);
        FormattedLogProvider logProvider = FormattedLogProvider.toOutputStream(System.out);
        GraphDatabaseDependencies graphDatabaseDependencies = GraphDatabaseDependencies.newDependencies().userLogProvider(logProvider);
        server = new CommunityNeoServer(defaults, factory, graphDatabaseDependencies, logProvider);
        server.start();
    }

    @AfterClass
    public static void stopServer() {
        server.stop();
    }

}
