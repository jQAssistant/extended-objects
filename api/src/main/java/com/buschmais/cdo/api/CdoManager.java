package com.buschmais.cdo.api;

import com.buschmais.cdo.api.QueryResult;

import java.util.Map;

public interface CdoManager {

    void begin();

    void commit();

    void rollback();

    <T> Iterable<T> find(Class<T> type, Object value);

    <T> T create(Class<T> type);

    <T, M> M migrate(T instance, Class<M> migrationTarget, MigrationHandler<T, M>... migrationHandlers);

    <T> void remove(T instance);

    QueryResult executeQuery(String query);

    QueryResult executeQuery(String query, Map<String, Object> parameters);

    void close();

    interface MigrationHandler<T, M> {
        void migrate(T instance, M target);
    }
}
