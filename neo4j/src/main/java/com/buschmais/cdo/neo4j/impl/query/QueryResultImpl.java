package com.buschmais.cdo.neo4j.impl.query;

import com.buschmais.cdo.api.IterableResult;
import com.buschmais.cdo.api.Query;
import org.apache.commons.io.IOUtils;

import java.io.Closeable;
import java.util.List;

/**
 * Represents the result of a createQuery.
 */
public class QueryResultImpl implements Closeable, Query.Result {

    /**
     * The column names returned by the createQuery.
     */
    private final List<String> columns;
    /**
     * The Iterable which can be used to scroll through the rows returned by the
     * createQuery.
     * <p>
     * Where applicable the values of a row are transformed to instances of the
     * corresponding classes.
     * </p>
     */
    private final IterableResult<Row> rows;

    /**
     * Constructor.
     *
     * @param columns A list containing the names of the returned columns.
     * @param rows    The rows.
     */
    public QueryResultImpl(List<String> columns, IterableResult<Row> rows) {
        this.columns = columns;
        this.rows = rows;
    }

    /**
     * Return the column names.
     *
     * @return The column names.
     */
    @Override
    public List<String> getColumns() {
        return columns;
    }

    /**
     * Return the {@link Iterable} to be used to scroll through the rows.
     *
     * @return The {@link Iterable} to be used to scroll through the rows.
     */
    @Override
    public IterableResult<Row> getRows() {
        return rows;
    }

    @Override
    public void close() {
        if (this.rows instanceof Closeable) {
            IOUtils.closeQuietly((Closeable) rows);
        }
    }

}
