package com.buschmais.cdo.api;

import java.io.Closeable;
import java.util.List;
import java.util.Map;

public interface Query {

    Query withParameter(String name, Object value);

    Query withParameters(Map<String, Object> parameters);

    <T> Result<T> execute();

    public interface Result<T> extends IterableResult<T>, AutoCloseable, Closeable {

        public List<String> getColumns();

        public interface CompositeRowObject extends CompositeObject {
            <C> C get(String name, Class<C> type);
        }
    }
}
