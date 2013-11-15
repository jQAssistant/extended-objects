package com.buschmais.cdo.api;

import java.io.Closeable;
import java.util.List;
import java.util.Map;

public interface QueryResult extends Closeable {

    List<String> getColumns();

    IterableResult<Row> getRows();

    /**
     * A row of a query result containing named columns and their values.
     */
    public static class Row {

        private Map<String, Object> row;

        public Row(Map<String, Object> row) {
            this.row = row;
        }

        @SuppressWarnings("unchecked")
        public <T> T get(String column, Class<T> type) {
            Object value = row.get(column);
            return value != null ? type.cast(value) : null;
        }

        public Map<String, Object> get() {
            return row;
        }
    }
}
