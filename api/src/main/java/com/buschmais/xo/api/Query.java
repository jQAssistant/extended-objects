package com.buschmais.xo.api;

import java.io.Closeable;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

/**
 * Defines a query using a fluent API.
 *
 * @param <T>
 *     The return type of the query.
 */
public interface Query<T> {

    /**
     * Specify the query language.
     *
     * @param queryLanguage
     *     The annotation class representing the query language.
     * @return The query.
     */
    Query<T> using(Class<? extends Annotation> queryLanguage);

    /**
     * Bind a parameter value to the query.
     *
     * @param name
     *     The parameter name.
     * @param value
     *     The parameter value.
     * @return The query.
     */
    Query<T> withParameter(String name, Object value);

    /**
     * Bind a map of parameter values to the query.
     *
     * @param parameters
     *     The map of parameters consisting of names as keys and their
     *     values.
     * @return The query.
     */
    Query<T> withParameters(Map<String, Object> parameters);

    /**
     * Set the flush behavior.
     *
     * @param flush
     *     if <code>true</code> any modified instances will be flushed to the
     *     datastore before executing the query.
     */
    Query<T> flush(boolean flush);

    /**
     * Execute the query.
     *
     * @return The {@link Query.Result} of the query.
     */
    Result<T> execute();

    /**
     * Defines the result of a query.
     *
     * @param <T>
     *     The type of the elements contained in the result.
     */
    interface Result<T> extends ResultIterable<T>, Closeable {

        @Override
        void close();

        /**
         * Defines the interface which is implemented by all instances contained in a
         * {@link ResultIterable} and which allows access to a column in a row.
         */
        interface CompositeRowObject extends CompositeObject {

            /**
             * Return the names of the columns in this row.
             *
             * @return The column names.
             */
            List<String> getColumns();

            /**
             * Return the value of column in row.
             *
             * @param name
             *     The column name.
             * @param type
             *     The type to be returned.
             * @param <C>
             *     The generic type to be returned.
             * @return The value.
             */
            <C> C get(String name, Class<C> type);
        }
    }
}
