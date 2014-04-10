package com.buschmais.xo.test;

import com.buschmais.xo.api.*;
import com.buschmais.xo.api.bootstrap.XO;
import com.buschmais.xo.api.bootstrap.XOUnit;
import org.junit.After;
import org.junit.Before;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import static com.buschmais.xo.api.Query.Result;
import static com.buschmais.xo.api.Query.Result.CompositeRowObject;

public abstract class AbstractXOManagerTest {

    public interface Database {
        URI getUri();
        Class<?> getProvider();
    }

    private XOUnit xoUnit;
    private XOManagerFactory xoManagerFactory;
    private XOManager xoManager = null;

    protected AbstractXOManagerTest(XOUnit xoUnit) {
        this.xoUnit = xoUnit;
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

    protected static Collection<Object[]> xoUnits(List<? extends Database> databases, List<? extends Class<?>> types) {
        return xoUnits(databases, types, Collections.<Class<?>>emptyList(), ValidationMode.AUTO, ConcurrencyMode.SINGLETHREADED, Transaction.TransactionAttribute.NONE);
    }

    protected static Collection<Object[]> xoUnits(List<? extends Database> databases, List<? extends Class<?>> types, List<? extends Class<?>> instanceListenerTypes, ValidationMode validationMode, ConcurrencyMode concurrencyMode, Transaction.TransactionAttribute transactionAttribute) {
        List<Object[]> xoUnits = new ArrayList<>(databases.size());
        for (Database database : databases) {
            XOUnit unit = new XOUnit("default", "Default XO unit", database.getUri(), database.getProvider(), new HashSet<>(types), instanceListenerTypes, validationMode, concurrencyMode, transactionAttribute, new Properties());
            xoUnits.add(new Object[]{unit});
        }
        return xoUnits;
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

    protected abstract void dropDatabase();

}
