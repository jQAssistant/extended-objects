package com.buschmais.cdo.neo4j.test;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.api.CdoManagerFactory;
import com.buschmais.cdo.api.QueryResult;
import com.buschmais.cdo.neo4j.impl.EmbeddedNeo4jCdoManagerFactoryImpl;
import org.junit.After;
import org.junit.Before;

import java.io.File;
import java.net.MalformedURLException;
import java.util.*;

public abstract class AbstractCdoManagerTest {

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
         * Return all rows.
         *
         * @return All rows.
         */
        public List<Map<String, Object>> getRows() {
            return rows;
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

    private CdoManagerFactory cdoManagerFactory;

    private CdoManager cdoManager;

    @Before
    public void createNodeManagerFactory() throws MalformedURLException {
        cdoManagerFactory = new EmbeddedNeo4jCdoManagerFactoryImpl(new File("target/neo4j").toURI().toURL(), getTypes());
        cdoManager = getCdoManagerFactory().createCdoManager();
        cdoManager.begin();
        cdoManager.executeQuery("MATCH (n)-[r]-(d) DELETE r");
        cdoManager.executeQuery("MATCH (n) DELETE n");
        cdoManager.commit();
    }

    @After
    public void closeNodeManagerFactory() {
        cdoManagerFactory.close();
    }

    /**
     * Executes a query and returns a {@link TestResult}.
     *
     * @param query The query.
     * @return The {@link TestResult}.
     */
    protected TestResult executeQuery(String query) {
        return executeQuery(query, Collections.<String, Object>emptyMap());
    }

    /**
     * Executes a query and returns a {@link TestResult}.
     *
     * @param query      The query.
     * @param parameters The query parameters.
     * @return The {@link TestResult}.
     */
    protected TestResult executeQuery(String query, Map<String, Object> parameters) {
        QueryResult queryResult = cdoManager.executeQuery(query, parameters);
        List<Map<String, Object>> rows = new ArrayList<>();
        Map<String, List<Object>> columns = new HashMap<>();
        for (String column : queryResult.getColumns()) {
            columns.put(column, new ArrayList<>());
        }
        for (QueryResult.Row row : queryResult.getRows()) {
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
        return cdoManager;
    }

    protected abstract Class<?>[] getTypes();
}
