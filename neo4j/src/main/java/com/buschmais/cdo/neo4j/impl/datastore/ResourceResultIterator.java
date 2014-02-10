package com.buschmais.cdo.neo4j.impl.datastore;

import com.buschmais.cdo.api.ResultIterator;
import org.neo4j.graphdb.ResourceIterator;

/**
 * A {@link ResultIterator} implement wrapping a {@link ResultIterator} provided by the Neo4j APIs.
 */
final class ResourceResultIterator<T> implements ResultIterator<T> {

    private final ResourceIterator<T> iterator;

    ResourceResultIterator(ResourceIterator<T> iterator) {
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
