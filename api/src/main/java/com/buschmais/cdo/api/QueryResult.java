package com.buschmais.cdo.api;

import java.util.List;
import java.util.Map;

public interface QueryResult {

    /**
     * A row of a query result containing named columns and their values.
     */
    public static class Row {

        private Map<String, Object> row;

        public Row(Map<String, Object> row) {
            this.row = row;
        }

        @SuppressWarnings("unchecked")
        public <T> T get(String column) {
            return (T) row.get(column);
        }

        public Map<String, Object> get() {
            return row;
        }
    }

    List<String> getColumns();

    Iterable<QueryResult.Row> getRows();

    void close();
}
