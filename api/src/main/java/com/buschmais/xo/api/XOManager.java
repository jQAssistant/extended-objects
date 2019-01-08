package com.buschmais.xo.api;

import static com.buschmais.xo.api.Query.Result.CompositeRowObject;
import static com.buschmais.xo.api.Transaction.TransactionAttribute.NOT_SUPPORTED;

import java.util.Set;

import javax.validation.ConstraintViolation;

/**
 * Defines methods to manage the lifecycle of property instances, query
 * execution and transaction management.
 */
public interface XOManager extends AutoCloseable, CloseSupport {

    /**
     * Return the {@link XOTransaction} associated with the manager.
     *
     * @return The {@link XOTransaction} or <code>null</code> if the datastore
     *         does not support transactions.
     */
    @Transaction(NOT_SUPPORTED)
    XOTransaction currentTransaction();

    /**
     * Performs a validation of all managed instances (JSR-303).
     *
     * @return The set of detected constraint violations.
     */
    Set<ConstraintViolation<Object>> validate();

    /**
     * Find an entity or relation using its it.
     *
     * @param type
     *            The type of the instance.
     * @param id
     *            The id.
     * @param <T>
     *            The type of the instance
     * @param <I>
     *            The type of the id.
     * @return The instance.
     */
    <T, I> T findById(Class<T> type, I id);

    /**
     * Find all instances according to the given type and value from an indexed
     * property.
     *
     * @param <T>
     *            The property type.
     * @param type
     *            The interface of the property type.
     * @param value
     *            The value.
     * @return An {@link Iterable} returning the property instance.
     */
    <T> ResultIterable<T> find(Class<T> type, Object value);

    /**
     * Find all instances according to the given type and and {@link Example}.
     *
     * @param <T>
     *            The property type.
     * @param type
     *            The interface of the property type.
     * @param example
     *            The {@link Example}.
     * @return An {@link Iterable} returning the property instance.
     */
    <T> ResultIterable<T> find(Example<T> example, Class<T> type);

    /**
     * Find all instances according to the given type and and {@link Example}.
     *
     * @param type
     *            The interface of the property type.
     * @param example
     *            The {@link Example}.
     * @return An {@link Iterable} returning the property instance.
     */
    ResultIterable<CompositeObject> find(Example<CompositeObject> example, Class<?> type, Class<?>... types);

    /**
     * Find all instances according to the given type and and {@link Example}.
     *
     * @param type
     *            The interface of the property type.
     * @param example
     *            The {@link Example}.
     * @return An {@link Iterable} returning the property instance.
     */
    <T> ResultIterable<T> find( Class<T> type, Example<T> example);

    /**
     * Create a new {@link CompositeObject} instance.
     *
     * @param type
     *            The interface the property type shall implement.
     * @param types
     *            Additional interfaces the entity type shall implement.
     * @return The {@link CompositeObject} instance.
     */
    CompositeObject create(Class<?> type, Class<?>... types);

    /**
     * Create a new {@link CompositeObject} instance using an example.
     *
     * @param example
     *            The example instance.
     * @param type
     *            The interface the property type shall implement.
     * @param types
     *            Additional interfaces the entity type shall implement.
     * @return The {@link CompositeObject} instance.
     */
    @Deprecated
    CompositeObject create(Example<CompositeObject> example, Class<?> type, Class<?>... types);


    /**
     * Create a new property instance.
     *
     * @param <T>
     *            The expected return type. Note that it must be assignable to
     *            at least one of the interfaces specified for the types.
     * @param type
     *            The interface the property type shall implement.
     * @return The property instance.
     */
    <T> T create(Class<T> type);

    /**
     * Create a new property instance using an example.
     *
     * @param example
     *            The example instance.
     * @param <T>
     *            The expected return type. Note that it must be assignable to
     *            at least one of the interfaces specified for the types.
     * @param type
     *            The interface the property type shall implement.
     * @return The property instance.
     */
    @Deprecated
    <T> T create(Example<T> example, Class<T> type);

    /**
     * Create a new {@link CompositeObject} instance using an example.
     *
     * @param type
     *            The interface the property type shall implement.
     * @param example
     *            The example instance.
     * @return The {@link CompositeObject} instance.
     */
    <T> T create(Class<T> type, Example<T> example);

    /**
     * Creates an instance of a typed relation between a source and a target
     * instance.
     *
     * @param source
     *            The source instance.
     * @param relationType
     *            The relation type.
     * @param target
     *            The target instance.
     * @param <S>
     *            The source type.
     * @param <R>
     *            The relation type.
     * @param <T>
     *            The target type.
     * @return The created relation instance.
     */
    <S, R, T> R create(S source, Class<R> relationType, T target);

    /**
     * Creates an instance of a typed relation between a source and a target
     * instance using an example.
     *
     * @param example
     *            The example instance.
     * @param source
     *            The source instance.
     * @param relationType
     *            The relation type.
     * @param target
     *            The target instance.
     * @param <S>
     *            The source type.
     * @param <R>
     *            The relation type.
     * @param <T>
     *            The target type.
     * @return The created relation instance.
     */
    @Deprecated
    <S, R, T> R create(Example<R> example, S source, Class<R> relationType, T target);

    /**
     * Creates an instance of a typed relation between a source and a target
     * instance using an example.
     *
     * @param source
     *            The source instance.
     * @param relationType
     *            The relation type.
     * @param target
     *            The target instance.
     * @param <S>
     *            The source type.
     * @param <R>
     *            The relation type.
     * @param <T>
     *            The target type.
     * @param example
     *            The example instance.
     * @return The created relation instance.
     */
    <S, R, T> R create(S source, Class<R> relationType, T target, Example<R> example);

    /**
     * Return a repository instance for the given type.
     *
     * @param repositoryType
     *            The repository type.
     * @param <T>
     *            The repository type.
     * @return The repository instance.
     */
    <T> T getRepository(Class<T> repositoryType);

    /**
     * Return the id of the given instance.
     */
    <T, Id> Id getId(T instance);

    /**
     * Return an {@link XOMigrator} instance that provides methods for type
     * migrations.
     *
     * @param instance
     *            The instance to migrate.
     * @param <T>
     *            The instance type.
     * @return The migrator.
     */
    <T> XOMigrator migrate(T instance);

    /**
     * Deletes a property instance.
     *
     * @param <T>
     *            The property type.
     * @param instance
     *            The instance.
     */
    <T> void delete(T instance);

    /**
     * Creates a {@link Query}.
     *
     * @param query
     *            The query expression.
     * @return The {@link Query}.
     */
    Query<CompositeRowObject> createQuery(String query);

    /**
     * Creates a typed {@link Query}.
     *
     * @param <T>
     *            The type to be returned.
     * @param query
     *            The query expression.
     * @param type
     *            The type to be returned.
     * @return The {@link Query}.
     */
    <T> Query<T> createQuery(String query, Class<T> type);

    /**
     * Creates a typed {@link Query}.
     *
     * @param query
     *            The query expression.
     * @param type
     *            The type to be returned.
     * @param types
     *            The types to be returned.
     * @return The {@link Query}.
     */
    Query<CompositeRowObject> createQuery(String query, Class<?> type, Class<?>... types);

    /**
     * Creates a typed {@link Query}.
     *
     * @param <T>
     *            The type to be returned.
     * @param query
     *            The query type.
     * @return The {@link Query}.
     */
    <T> Query<T> createQuery(Class<T> query);

    /**
     * Creates a typed {@link Query}.
     *
     * @param query
     *            The query type.
     * @param types
     *            The additional types to be returned.
     * @return The {@link Query}.
     */
    <Q> Query<CompositeRowObject> createQuery(Class<Q> query, Class<?>... types);

    /**
     * Close the {@link com.buschmais.xo.api.XOManager}.
     */
    @Transaction(NOT_SUPPORTED)
    void close();

    /**
     * Return the underlying datastore session.
     *
     * @param sessionType
     *            The expected session type.
     * @param <DS>
     *            The expected session type.
     * @return The expected session type.
     */
    @Transaction(NOT_SUPPORTED)
    <DS> DS getDatastoreSession(Class<DS> sessionType);

    /**
     * Flushes all pending changes to the datastore.
     */
    void flush();

    /**
     * Clear caches, including pending changes.
     */
    void clear();

    /**
     * Register an instance listener containing life cycle methods (e.g.
     * annotated with {@link com.buschmais.xo.api.annotation.PostCreate}.
     *
     * @param instanceListener
     *            The instance listener.
     * @param <I>
     *            The instance listener type.
     */
    @Transaction(NOT_SUPPORTED)
    <I> void registerInstanceListener(I instanceListener);
}
