package com.buschmais.cdo.neo4j.impl.query;

import com.buschmais.cdo.api.Query;

import java.util.Map;

public class QueryResultRow implements Query.Result.Row {

    private Map<String, Object> row;

    public QueryResultRow(Map<String, Object> row) {
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
