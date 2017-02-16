package com.buschmais.xo.test;

import static com.buschmais.xo.api.Query.Result;
import static com.buschmais.xo.api.Query.Result.CompositeRowObject;

import java.net.URI;
import java.util.*;

import org.junit.After;
import org.junit.Before;

import com.buschmais.xo.api.*;
import com.buschmais.xo.api.bootstrap.XO;
import com.buschmais.xo.api.bootstrap.XOUnit;

/**
 * Abstract base class for parametrized XO tests.
 */
public abstract class AbstractXOManagerTest {

    /**
     * Defines a database under test. Intended to be implemented by enum types.
     */
    public interface Database {

        URI getUri();

        Class<?> getProvider();

        Map<String, Object> getProperties();

    }

    private XOUnit xoUnit;
    private XOManagerFactory xoManagerFactory;
    private XOManager xoManager = null;

    /**
     * Return a collection of {@link com.buschmais.xo.api.bootstrap.XOUnit}s initialized with the given parameters.
     *
     * @param databases The databases to use.
     * @param types     The types to register.
     * @return The collection of {@link com.buschmais.xo.api.bootstrap.XOUnit}s.
     */
    protected static Collection<Object[]> xoUnits(List<? extends Database> databases, List<? extends Class<?>> types) {
        return xoUnits(databases, types, Collections.<Class<?>>emptyList(), ValidationMode.AUTO, ConcurrencyMode.SINGLETHREADED, Transaction.TransactionAttribute.NONE);
    }

    /**
     * Return a collection of {@link com.buschmais.xo.api.bootstrap.XOUnit}s initialized with the given parameters.
     *
     * @param databases             The databases to use.
     * @param types                 The types to register.
     * @param instanceListenerTypes The instance listeners to register.
     * @param validationMode        The validation mode to use.
     * @param concurrencyMode       The concurrency mode to use.
     * @param transactionAttribute  The transaction attribute to use.
     * @return The collection of {@link com.buschmais.xo.api.bootstrap.XOUnit}s.
     */
    protected static Collection<Object[]> xoUnits(List<? extends Database> databases, List<? extends Class<?>> types, List<? extends Class<?>> instanceListenerTypes, ValidationMode validationMode, ConcurrencyMode concurrencyMode, Transaction.TransactionAttribute transactionAttribute) {
        List<Object[]> xoUnits = new ArrayList<>(databases.size());
        for (Database database : databases) {
            XOUnit unit = xoUnit(database, types, instanceListenerTypes, validationMode, concurrencyMode, transactionAttribute);
            xoUnits.add(new Object[]{unit});
        }
        return xoUnits;
    }

    /**
     * Return a {@link com.buschmais.xo.api.bootstrap.XOUnit} initialized with the given parameters.
     *
     * @param types The types to register.
     * @return The {@link com.buschmais.xo.api.bootstrap.XOUnit}.
     */
    protected static XOUnit xoUnit(Database database, List<? extends Class<?>> types) {
        return xoUnit(database, types, Collections.<Class<?>>emptyList(), ValidationMode.AUTO, ConcurrencyMode.SINGLETHREADED, Transaction.TransactionAttribute.NONE);
    }

    /**
     * Return a {@link com.buschmais.xo.api.bootstrap.XOUnit} initialized with the given parameters.
     *
     * @param types                 The types to register.
     * @param instanceListenerTypes The instance listeners to register.
     * @param validationMode        The validation mode to use.
     * @param concurrencyMode       The concurrency mode to use.
     * @param transactionAttribute  The transaction attribute to use.
     * @return The {@link com.buschmais.xo.api.bootstrap.XOUnit}.
     */
    protected static XOUnit xoUnit(Database database, List<? extends Class<?>> types, List<? extends Class<?>> instanceListenerTypes, ValidationMode validationMode, ConcurrencyMode concurrencyMode, Transaction.TransactionAttribute transactionAttribute) {
        return new XOUnit("default", "Default XO unit", database.getUri(), database.getProvider(), new HashSet<>(types), instanceListenerTypes, validationMode, concurrencyMode, transactionAttribute, new Properties());
    }

    /**
     * Parametrized constructor.
     *
     * @param xoUnit The {@link com.buschmais.xo.api.bootstrap.XOUnit} to use.
     */
    protected AbstractXOManagerTest(XOUnit xoUnit) {
        this.xoUnit = xoUnit;
    }

    /**
     * Creates the {@link com.buschmais.xo.api.XOManagerFactory}.
     */
    @Before
    public void createXOManagerFactory() {
        xoManagerFactory = XO.createXOManagerFactory(xoUnit);
        dropDatabase();
    }

    /**
     * Closes the {@link com.buschmais.xo.api.XOManagerFactory}.
     */
    @After
    public void closeXOManagerFactory() {
        closeXOmanager();
        if (xoManagerFactory != null) {
            xoManagerFactory.close();
        }
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
     * Executes a query and returns a {@link TestResult}.
     *
     * @param query      The query.
     * @param parameters The parameters.
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

    /**
     * Return the {@link com.buschmais.xo.api.XOManagerFactory}.
     *
     * @return The {@link com.buschmais.xo.api.XOManagerFactory}.
     */
    protected XOManagerFactory getXoManagerFactory() {
        return xoManagerFactory;
    }

    /**
     * Return the current {@link com.buschmais.xo.api.XOManager}.
     *
     * @return The current {@link com.buschmais.xo.api.XOManager}.
     */
    protected XOManager getXoManager() {
        if (xoManager == null) {
            xoManager = getXoManagerFactory().createXOManager();
        }
        return xoManager;
    }

    /**
     * Close the current {@link com.buschmais.xo.api.XOManager}.
     */
    protected void closeXOmanager() {
        if (xoManager != null) {
            XOTransaction transaction = xoManager.currentTransaction();
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
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

    /**
     * Drop all data from the database.
     */
    protected abstract void dropDatabase();

}
