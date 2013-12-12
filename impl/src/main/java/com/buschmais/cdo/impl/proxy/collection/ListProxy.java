package com.buschmais.cdo.impl.proxy.collection;

import java.util.AbstractSequentialList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ListProxy<Instance> extends AbstractSequentialList<Instance> implements List<Instance> {

    private CollectionProxy<Instance, ?> collectionProxy;

    public ListProxy(CollectionProxy<Instance, ?> collectionProxy) {
        this.collectionProxy = collectionProxy;
    }

    @Override
    public ListIterator<Instance> listIterator(int index) {
        final Iterator<Instance> iterator = collectionProxy.iterator();
        return new ListIterator<Instance>() {
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public Instance next() {
                return iterator.next();
            }

            @Override
            public boolean hasPrevious() {
                throw new UnsupportedOperationException("Operation not supported.");
            }

            @Override
            public Instance previous() {
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
            public void set(Instance instance) {
                throw new UnsupportedOperationException("Operation not supported.");
            }

            @Override
            public void add(Instance instance) {
                throw new UnsupportedOperationException("Operation not supported.");
            }
        };
    }

    @Override
    public int size() {
        return collectionProxy.size();
    }

    @Override
    public boolean add(Instance instance) {
        return collectionProxy.add(instance);
    }

    @Override
    public boolean remove(Object o) {
        return collectionProxy.remove(o);
    }
}
