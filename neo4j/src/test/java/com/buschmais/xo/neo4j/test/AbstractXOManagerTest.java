package com.buschmais.xo.neo4j.test;

import com.buschmais.xo.api.*;
import com.buschmais.xo.api.bootstrap.XO;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.api.Neo4jXOProvider;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.kernel.GraphDatabaseAPI;
import org.neo4j.server.WrappingNeoServer;
import org.neo4j.test.TestGraphDatabaseFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import static com.buschmais.xo.api.Query.Result;
import static com.buschmais.xo.api.Query.Result.CompositeRowObject;
import static com.buschmais.xo.neo4j.test.AbstractXOManagerTest.Database.MEMORY;
import static com.buschmais.xo.neo4j.test.AbstractXOManagerTest.Database.REST;

public abstract class AbstractXOManagerTest {

    protected enum Database {
        MEMORY("memory:///"),
        REST("http://localhost:7474/db/data");
        private URI uri;

        private Database(String uri) {
            try {
                this.uri = new URI(uri);
            } catch (URISyntaxException e) {
                throw new IllegalArgumentException(e);
            }
        }

        public URI getUri() {
            return uri;
        }
    }

    private static WrappingNeoServer server;

    private XOUnit xoUnit;
    private XOManagerFactory xoManagerFactory;
    private XOManager xoManager = null;

    protected AbstractXOManagerTest(XOUnit xoUnit) {
        this.xoUnit = xoUnit;
    }

    @BeforeClass
    public static void startServer() {
        GraphDatabaseService graphDatabaseService = new TestGraphDatabaseFactory().newImpermanentDatabase();
        server = new WrappingNeoServer((GraphDatabaseAPI) graphDatabaseService);
        server.start();
    }

    @Before
    public void createXOManagerFactory() throws URISyntaxException {
        xoManagerFactory = XO.createXOManagerFactory(xoUnit);
        dropDatabase();
    }

    @After
    public void closeXOManagerFactory() {
        closeXOmanager();
        if (xoManagerFactory != null) {
            xoManagerFactory.close();
        }
    }

    protected static Collection<Object[]> xoUnits(Class<?>... types) {
        return xoUnits(Arrays.asList(MEMORY, REST), Arrays.asList(types), Collections.<Class<?>>emptyList(), ValidationMode.AUTO, ConcurrencyMode.SINGLETHREADED, Transaction.TransactionAttribute.MANDATORY);
    }

    protected static Collection<Object[]> xoUnits(List<Database> databases, List<? extends Class<?>> types) {
        return xoUnits(databases, types, Collections.<Class<?>>emptyList(), ValidationMode.AUTO, ConcurrencyMode.SINGLETHREADED, Transaction.TransactionAttribute.MANDATORY);
    }

    protected static Collection<Object[]> xoUnits(List<? extends Class<?>> types, List<? extends Class<?>> instanceListeners, ValidationMode validationMode, ConcurrencyMode concurrencyMode, Transaction.TransactionAttribute transactionAttribute) {
        return xoUnits(Arrays.asList(MEMORY, REST), types, instanceListeners, validationMode, concurrencyMode, transactionAttribute);
    }

    protected static Collection<Object[]> xoUnits(List<Database> databases, List<? extends Class<?>> types, List<? extends Class<?>> instanceListenerTypes, ValidationMode valiationMode, ConcurrencyMode concurrencyMode, Transaction.TransactionAttribute transactionAttribute) {
        List<Object[]> xoUnits = new ArrayList<>(databases.size());
        for (Database database : databases) {
            XOUnit unit = new XOUnit("default", "Default XO unit", database.getUri(), Neo4jXOProvider.class, new HashSet<>(types), instanceListenerTypes, valiationMode, concurrencyMode, transactionAttribute, new Properties());
            xoUnits.add(new Object[]{unit});
        }
        return xoUnits;
    }

    @AfterClass
    public static void stopServer() {
        server.stop();
    }

    /**
     * Executes a createQuery and returns a {@link TestResult}.
     *
     * @param query The createQuery.
     * @return The {@link TestResult}.
     */
    protected TestResult executeQuery(String query) {
        return executeQuery(query, Collections.<String, Object>emptyMap());
    }

    /**
     * Executes a createQuery and returns a {@link TestResult}.
     *
     * @param query      The createQuery.
     * @param parameters The createQuery parameters.
     * @return The {@link TestResult}.
     */
    protected TestResult executeQuery(String query, Map<String, Object> parameters) {
        Result<CompositeRowObject> result = xoManager.createQuery(query).withParameters(parameters).execute();
        Map<String, List<Object>> columns = new HashMap<>();
        for (CompositeRowObject row : result) {
            Iterable<String> columnNames = row.getColumns();
            for (String columnName : columnNames) {
                List<Object> columnValues = columns.get(columnName);
                if (columnValues == null) {
                    columnValues = new ArrayList<>();
                    columns.put(columnName, columnValues);
                }
                columnValues.add(row.get(columnName, Object.class));
            }
        }
        return new TestResult(columns);
    }

    protected XOManagerFactory getXoManagerFactory() {
        return xoManagerFactory;
    }

    protected XOManager getXoManager() {
        if (xoManager == null) {
            xoManager = getXoManagerFactory().createXOManager();
        }
        return xoManager;
    }

    protected void closeXOmanager() {
        if (xoManager != null) {
            if (xoManager.currentTransaction().isActive()) {
                xoManager.currentTransaction().rollback();
            }
            xoManager.close();
            xoManager = null;
        }
    }

    private void dropDatabase() {
        XOManager manager = getXoManager();
        manager.currentTransaction().begin();
        manager.createQuery("MATCH (n)-[r]-() DELETE r").execute();
        manager.createQuery("MATCH (n) DELETE n").execute();
        manager.currentTransaction().commit();
    }


    /**
     * Represents a test result which allows fetching values by row or columns.
     */
    protected class TestResult {

        private Map<String, List<Object>> columns;

        TestResult(Map<String, List<Object>> columns) {
            this.columns = columns;
        }

        /**
         * Return a column identified by its name.
         *
         * @param <T> The expected type.
         * @return All columns.
         */
        public <T> List<T> getColumn(String name) {
            return (List<T>) columns.get(name);
        }
    }
}
