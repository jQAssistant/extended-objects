package com.buschmais.cdo.neo4j.impl.node.proxy.collection;

import com.buschmais.cdo.neo4j.impl.node.InstanceManager;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;

import java.util.AbstractSet;
import java.util.Iterator;

public class SetProxy<E> extends AbstractSet<E> {

    private CollectionProxy<E> collectionProxy;

    public SetProxy(CollectionProxy<E> collectionProxy) {
        this.collectionProxy = collectionProxy;
    }

    @Override
    public Iterator<E> iterator() {
        return collectionProxy.iterator();
    }

    @Override
    public int size() {
        return collectionProxy.size();
    }

    @Override
    public boolean add(E e) {
        if (contains(e)) {
            return false;
        }
        return collectionProxy.add(e);
    }

    @Override
    public boolean remove(Object o) {
        return collectionProxy.remove(o);
    }
}
