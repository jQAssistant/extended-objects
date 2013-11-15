package com.buschmais.cdo.api;

import java.io.Closeable;
import java.util.List;
import java.util.Map;

public interface Query {

    Query setParameter(String name, Object value);

    Query setParameters(Map<String, Object> parameters);

    Query setExpression(String query);

    Result execute();

    public interface Result extends Closeable {

        List<String> getColumns();

        IterableResult<Row> getRows();

        /**
         * A row of a createQuery result containing named columns and their values.
         */
        public interface Row {

            public <T> T get(String column, Class<T> type);

            public Map<String, Object> get();
        }
    }

}
