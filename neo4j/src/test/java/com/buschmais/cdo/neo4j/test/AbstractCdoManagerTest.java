package com.buschmais.cdo.neo4j.test;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.api.CdoManagerFactory;
import com.buschmais.cdo.api.Query;
import com.buschmais.cdo.neo4j.impl.EmbeddedNeo4jCdoManagerFactoryImpl;
import org.junit.After;
import org.junit.Before;

import java.io.File;
import java.net.MalformedURLException;
import java.util.*;

public abstract class AbstractCdoManagerTest {

    private CdoManagerFactory cdoManagerFactory;
    private CdoManager cdoManager = null;

    @Before
    public void createNodeManagerFactory() throws MalformedURLException {
        cdoManagerFactory = new EmbeddedNeo4jCdoManagerFactoryImpl(new File("target/neo4j").toURI().toURL(), getTypes());
        dropDatabase();
    }

    private void dropDatabase() {
        CdoManager manager = getCdoManager();
        manager.begin();
        manager.createQuery("MATCH (n)-[r]-() DELETE r").execute();
        manager.createQuery("MATCH (n) DELETE n").execute();
        manager.commit();
    }

    @After
    public void closeNodeManagerFactory() {
        closeCdoManager();
        cdoManagerFactory.close();
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
        Query.Result queryResult = cdoManager.createQuery(query).setParameters(parameters).execute();
        List<Map<String, Object>> rows = new ArrayList<>();
        Map<String, List<Object>> columns = new HashMap<>();
        for (String column : queryResult.getColumns()) {
            columns.put(column, new ArrayList<>());
        }
        for (Query.Result.Row row : queryResult.getRows()) {
            Map<String, Object> rowData = row.get();
            rows.add(rowData);
            for (Map.Entry<String, ?> entry : rowData.entrySet()) {
                List<Object> column = columns.get(entry.getKey());
                column.add(entry.getValue());
            }
        }
        return new TestResult(rows, columns);
    }

    protected CdoManagerFactory getCdoManagerFactory() {
        return cdoManagerFactory;
    }

    protected CdoManager getCdoManager() {
        if (cdoManager == null) {
            cdoManager = getCdoManagerFactory().createCdoManager();
        }
        return cdoManager;
    }

    protected void closeCdoManager() {
        if (cdoManager != null) {
            cdoManager.close();
        }
    }

    protected abstract Class<?>[] getTypes();


    /**
     * Represents a test result which allows fetching values by row or columns.
     */
    protected class TestResult {
        private List<Map<String, Object>> rows;
        private Map<String, List<Object>> columns;

        TestResult(List<Map<String, Object>> rows, Map<String, List<Object>> columns) {
            this.rows = rows;
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
