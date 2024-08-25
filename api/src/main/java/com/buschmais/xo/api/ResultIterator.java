package com.buschmais.xo.api;

import java.io.Closeable;
import java.util.Iterator;

/**
 * An {@link Iterator} which extends {@link AutoCloseable} and
 * {@link Closeable}.
 *
 * @param <T>
 *     The type returned by the {@link Iterator}.
 */
public interface ResultIterator<T> extends Iterator<T>, AutoCloseable, Closeable {

    @Override
    void close();
}
