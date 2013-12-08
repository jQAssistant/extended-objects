package com.buschmais.cdo.neo4j.impl.node.proxy.collection;

import java.util.AbstractSet;
import java.util.Iterator;

public class SetProxy<T> extends AbstractSet<T> {

    private CollectionProxy<T, ?> collectionProxy;

    public SetProxy(CollectionProxy<T, ?> collectionProxy) {
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
