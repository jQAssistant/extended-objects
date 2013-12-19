package com.buschmais.cdo.api;

import java.io.Closeable;
import java.util.Collection;
import java.util.Map;

/**
 * Defines a query using a fluent API.
 *
 * @param <T> The return type of the query.
 */
public interface Query<T> {

    /**
     * Bind a parameter value to the query.
     *
     * @param name  The parameter name.
     * @param value The parameter value.
     * @return The query.
     */
    Query withParameter(String name, Object value);

    /**
     * Bind a map of parameter values to the query.
     *
     * @param parameters The map of parameters consisting of names as keys and their values.
     * @return The query.
     */
    Query withParameters(Map<String, Object> parameters);

    /**
     * Execute the query.
     *
     * @return The {@link Query.Result} of the query.
     */
    Result<T> execute();

    /**
     * Defines the result of a query.
     *
     * @param <T> The type of the elements contained in the result.
     */
    public interface Result<T> extends ResultIterable<T>, AutoCloseable, Closeable {

        /**
         * Defines the interface which is implemented by all instances contained in a {@link ResultIterable} and which allows access to a column in a row.
         */
        public interface CompositeRowObject extends CompositeObject {

            /**
             * Return the names of the columns in this row.
             *
             * @return The column names.
             */
            public Collection<String> getColumns();

            /**
             * Return the value of column in row.
             *
             * @param name The column name.
             * @param type The type to be returned.
             * @param <C>  The generic type to be returned.
             * @return The value.
             */
            <C> C get(String name, Class<C> type);
        }
    }
}
