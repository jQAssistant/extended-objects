package com.buschmais.cdo.api;

import java.util.Map;

/**
 * Defines methods to manage the lifecycle of composite instances, query execution and transaction management.
 */
public interface CdoManager {

    /**
     * Begin a transaction.
     */
    void begin();

    /**
     * Commit all changes of the current transaction.
     */
    void commit();

    /**
     * Rollback all changes from the current transaction.
     */
    void rollback();

    /**
     * Find all composite instances according to the given type and value (e.g. from an index).
     *
     * @param <T>   The composite type.
     * @param type  The interface of the composite type.
     * @param value The value.
     * @return An {@Iterable} returning the composite instance.
     */
    <T> Iterable<T> find(Class<T> type, Object value);

    /**
     * Create a new composite instance.
     *
     * @param <T>   The expected return type. Note that it must be assignable to at least one of the interfaces specified for the types.
     * @param types The interfaces the composite type shall implement.
     * @return The composite instance.
     */
    <T> T create(Class<?>... types);

    /**
     * Migrates the type of a composite instance to the given target and returns it. The original instance will not be usable anymore after migration.
     *
     * @param <T>              The composite type.
     * @param <M>              The migrated composite type. Note that it be assignable to at least one of the interfaces specified for types.
     * @param instance         The instance.
     * @param targetTypes      The target interfaces which shall be implemented by the migrated instance..
     * @return The migrated instance.
     */
    <T, M> M migrate(T instance, Class<?>... targetTypes);

    /**
     * Migrates the type of a composite instance to the given target and returns it. The original instance will not be usable anymore after migration.
     *
     * @param <T>              The composite type.
     * @param <M>              The migrated composite type. Note that it be assignable to at least one of the interfaces specified for types.
     * @param instance         The instance.
     * @param migrationHandler The {@link MigrationHandler} to be used to migrate data (e.g. properties) to the new type.
     * @param targetTypes      The target interfaces which shall be implemented by the migrated instance..
     * @return The migrated instance.
     */
    <T, M> M migrate(T instance, MigrationHandler<T, M> migrationHandler, Class<?>... targetTypes);

    /**
     * Deletes a composite instance.
     *
     * @param <T>      The composite type.
     * @param instance The instance.
     */
    <T> void delete(T instance);

    QueryResult executeQuery(String query);

    QueryResult executeQuery(String query, Map<String, Object> parameters);

    /**
     * Close the {@CdoManager}.
     */
    void close();

    interface MigrationHandler<T, M> {
        void migrate(T instance, M target);
    }
}
