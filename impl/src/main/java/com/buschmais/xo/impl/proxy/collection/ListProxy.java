package com.buschmais.xo.impl.proxy.collection;

import java.util.AbstractSequentialList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ListProxy<Instance> extends AbstractSequentialList<Instance> implements List<Instance> {

    private final AbstractCollectionProxy<Instance, ?, ?, ?> collectionProxy;

    public ListProxy(AbstractCollectionProxy<Instance, ?, ?, ?> collectionProxy) {
        this.collectionProxy = collectionProxy;
    }

    @Override
    public ListIterator<Instance> listIterator(int index) {
        final Iterator<Instance> iterator = collectionProxy.iterator();
        for (int i = 0; i < index && iterator.hasNext(); i++) {
            iterator.next();
        }
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
                return unsupportedOperation();
            }

            @Override
            public Instance previous() {
                return unsupportedOperation();
            }

            @Override
            public int nextIndex() {
                return unsupportedOperation();
            }

            @Override
            public int previousIndex() {
                return unsupportedOperation();
            }

            @Override
            public void remove() {
                unsupportedOperation();
            }

            @Override
            public void set(Instance instance) {
                unsupportedOperation();
            }

            @Override
            public void add(Instance instance) {
                unsupportedOperation();
            }

            private <T> T unsupportedOperation() {
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
