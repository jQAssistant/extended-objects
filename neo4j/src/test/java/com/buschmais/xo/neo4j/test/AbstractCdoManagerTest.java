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
import static com.buschmais.xo.neo4j.test.AbstractCdoManagerTest.Database.MEMORY;
import static com.buschmais.xo.neo4j.test.AbstractCdoManagerTest.Database.REST;

public abstract class AbstractCdoManagerTest {

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

    private XOUnit XOUnit;
    private XOManagerFactory XOManagerFactory;
    private XOManager XOManager = null;

    protected AbstractCdoManagerTest(XOUnit XOUnit) {
        this.XOUnit = XOUnit;
    }

    @BeforeClass
    public static void startServer() {
        GraphDatabaseService graphDatabaseService = new TestGraphDatabaseFactory().newImpermanentDatabase();
        server = new WrappingNeoServer((GraphDatabaseAPI) graphDatabaseService);
        server.start();
    }

    @Before
    public void createCdoManagerFactory() throws URISyntaxException {
        XOManagerFactory = XO.createXOManagerFactory(XOUnit);
        dropDatabase();
    }

    @After
    public void closeNodeManagerFactory() {
        closeCdoManager();
        if (XOManagerFactory != null) {
            XOManagerFactory.close();
        }
    }

    protected static Collection<Object[]> cdoUnits(Class<?>... types) {
        return cdoUnits(Arrays.asList(MEMORY, REST), Arrays.asList(types), Collections.<Class<?>>emptyList(), ValidationMode.AUTO, ConcurrencyMode.SINGLETHREADED, Transaction.TransactionAttribute.MANDATORY);
    }

    protected static Collection<Object[]> cdoUnits(List<Database> databases, List<? extends Class<?>> types) {
        return cdoUnits(databases, types, Collections.<Class<?>>emptyList(), ValidationMode.AUTO, ConcurrencyMode.SINGLETHREADED, Transaction.TransactionAttribute.MANDATORY);
    }

    protected static Collection<Object[]> cdoUnits(List<? extends Class<?>> types, List<? extends Class<?>> instanceListeners, ValidationMode validationMode, ConcurrencyMode concurrencyMode, Transaction.TransactionAttribute transactionAttribute) {
        return cdoUnits(Arrays.asList(MEMORY, REST), types, instanceListeners, validationMode, concurrencyMode, transactionAttribute);
    }

    protected static Collection<Object[]> cdoUnits(List<Database> databases, List<? extends Class<?>> types, List<? extends Class<?>> instanceListenerTypes, ValidationMode valiationMode, ConcurrencyMode concurrencyMode, Transaction.TransactionAttribute transactionAttribute) {
        List<Object[]> cdoUnits = new ArrayList<>(databases.size());
        for (Database database : databases) {
            XOUnit unit = new XOUnit("default", "Default CDO unit", database.getUri(), Neo4jXOProvider.class, new HashSet<>(types), instanceListenerTypes, valiationMode, concurrencyMode, transactionAttribute, new Properties());
            cdoUnits.add(new Object[]{unit});
        }
        return cdoUnits;
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
        Result<CompositeRowObject> result = XOManager.createQuery(query).withParameters(parameters).execute();
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

    protected XOManagerFactory getXOManagerFactory() {
        return XOManagerFactory;
    }

    protected XOManager getXOManager() {
        if (XOManager == null) {
            XOManager = getXOManagerFactory().createXOManager();
        }
        return XOManager;
    }

    protected void closeCdoManager() {
        if (XOManager != null) {
            if (XOManager.currentTransaction().isActive()) {
                XOManager.currentTransaction().rollback();
            }
            XOManager.close();
            XOManager = null;
        }
    }

    private void dropDatabase() {
        XOManager manager = getXOManager();
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
