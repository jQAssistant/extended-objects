package com.buschmais.xo.neo4j.test;

import static com.buschmais.xo.neo4j.test.Neo4jDatabase.MEMORY;
import static org.neo4j.server.database.Database.Factory;

import java.util.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.kernel.GraphDatabaseDependencies;
import org.neo4j.kernel.configuration.Config;
import org.neo4j.kernel.impl.factory.GraphDatabaseFacade;
import org.neo4j.logging.FormattedLogProvider;
import org.neo4j.logging.NullLogProvider;
import org.neo4j.server.CommunityNeoServer;
import org.neo4j.test.TestGraphDatabaseFactory;

import com.buschmais.xo.api.ConcurrencyMode;
import com.buschmais.xo.api.Transaction;
import com.buschmais.xo.api.ValidationMode;
import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.test.AbstractXOManagerTest;

public abstract class AbstractNeo4jXOManagerTest extends AbstractXOManagerTest {

    private static CommunityNeoServer server;

    protected AbstractNeo4jXOManagerTest(XOUnit xoUnit) {
        super(xoUnit);
    }

    @BeforeClass
    public static void startServer() {
        GraphDatabaseService databaseService = new TestGraphDatabaseFactory().newImpermanentDatabase();
        Factory factory = (config, dependencies) -> new org.neo4j.server.database.Database() {
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
        Config defaults = new Config(opts);
        FormattedLogProvider logProvider = FormattedLogProvider.toOutputStream(System.out);
        GraphDatabaseDependencies graphDatabaseDependencies = GraphDatabaseDependencies.newDependencies().userLogProvider(logProvider);
        server = new CommunityNeoServer(defaults, graphDatabaseDependencies, logProvider);
        //server = new CommunityNeoServer(Config.empty(), factory, GraphDatabaseDependencies.newDependencies(), NullLogProvider.getInstance());
        server.start();
    }

    protected static Collection<Object[]> xoUnits(Class<?>... types) {
        return xoUnits(Arrays.asList(MEMORY), Arrays.asList(types), Collections.<Class<?>> emptyList(), ValidationMode.AUTO, ConcurrencyMode.SINGLETHREADED,
                Transaction.TransactionAttribute.NONE);
    }

    protected static Collection<Object[]> xoUnits(List<? extends Class<?>> types, List<? extends Class<?>> instanceListeners, ValidationMode validationMode,
            ConcurrencyMode concurrencyMode, Transaction.TransactionAttribute transactionAttribute) {
        return xoUnits(Arrays.asList(MEMORY), types, instanceListeners, validationMode, concurrencyMode, transactionAttribute);
    }

    @AfterClass
    public static void stopServer() {
        server.stop();
    }

    protected void dropDatabase() {
        XOManager manager = getXoManager();
        manager.currentTransaction().begin();
        manager.createQuery("MATCH (n)-[r]-() DELETE r").execute();
        manager.createQuery("MATCH (n) DELETE n").execute();
        manager.currentTransaction().commit();
    }
}
