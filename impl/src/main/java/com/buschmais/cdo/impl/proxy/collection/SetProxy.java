package com.buschmais.cdo.impl.proxy.collection;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;

public class SetProxy<T> extends AbstractSet<T> implements Set<T> {

    private AbstractCollectionProxy<T, ?, ?, ?> collectionProxy;

    public SetProxy(AbstractCollectionProxy<T, ?, ?, ?> collectionProxy) {
        this.collectionProxy = collectionProxy;
    }

    @Override
    public Iterator<T> iterator() {
        return collectionProxy.iterator();
    }

    @Override
    public int size() {
        return collectionProxy.size();
    }

    @Override
    public boolean add(T t) {
        if (contains(t)) {
            return false;
        }
        return collectionProxy.add(t);
    }

    @Override
    public boolean remove(Object o) {
        return collectionProxy.remove(o);
    }
}
