package com.buschmais.cdo.neo4j.impl.node.proxy.collection;

import java.util.AbstractSequentialList;
import java.util.Iterator;
import java.util.ListIterator;

public class ListProxy<E> extends AbstractSequentialList<E> {

    private CollectionProxy<E> collectionProxy;

    public ListProxy(CollectionProxy<E> collectionProxy) {
        this.collectionProxy = collectionProxy;
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        final Iterator<E> iterator = collectionProxy.iterator();
        return new ListIterator<E>() {
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public E next() {
                return iterator.next();
            }

            @Override
            public boolean hasPrevious() {
                throw new UnsupportedOperationException("Operation not supported.");
            }

            @Override
            public E previous() {
                throw new UnsupportedOperationException("Operation not supported.");
            }

            @Override
            public int nextIndex() {
                throw new UnsupportedOperationException("Operation not supported.");
            }

            @Override
            public int previousIndex() {
                throw new UnsupportedOperationException("Operation not supported.");
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Operation not supported.");
            }

            @Override
            public void set(E e) {
                throw new UnsupportedOperationException("Operation not supported.");
            }

            @Override
            public void add(E e) {
                throw new UnsupportedOperationException("Operation not supported.");
            }
        };
    }

    @Override
    public int size() {
        return collectionProxy.size();
    }

    @Override
    public boolean add(E e) {
        return collectionProxy.add(e);
    }

    @Override
    public boolean remove(Object o) {
        return collectionProxy.remove(o);
    }
}
