package com.buschmais.xo.neo4j.impl.datastore.query;

import org.neo4j.graphdb.ResourceIterator;

import com.buschmais.xo.api.ResultIterator;

/**
 * A {@link ResultIterator} implement wrapping a {@link ResultIterator} provided by the Neo4j APIs.
 */
public final class ResourceResultIterator<T> implements ResultIterator<T> {

    private final ResourceIterator<T> iterator;

    public ResourceResultIterator(final ResourceIterator<T> iterator) {
        this.iterator = iterator;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public T next() {
        return iterator.next();
    }

    @Override
    public void remove() {
        iterator.remove();
    }

    @Override
    public void close() {
        iterator.close();
    }
}
