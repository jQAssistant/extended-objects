package com.buschmais.cdo.api;

import java.util.List;

public interface IterableQueryResult<T> extends IterableResult<T> {

    public List<String> getColumns();

    public interface Row {
        <C> C get(String name, Class<C> type);
    }
}
