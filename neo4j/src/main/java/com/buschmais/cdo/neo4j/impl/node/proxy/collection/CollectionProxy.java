package com.buschmais.cdo.neo4j.impl.node.proxy.collection;

import com.buschmais.cdo.neo4j.impl.node.InstanceManager;
import com.buschmais.cdo.neo4j.impl.node.proxy.method.property.RelationshipManager;
import org.neo4j.graphdb.Node;

import java.util.AbstractCollection;
import java.util.Iterator;

public class CollectionProxy<E> extends AbstractCollection<E> {

    private Node node;
    private RelationshipManager relationshipManager;
    private InstanceManager<Long, Node> instanceManager;

    public CollectionProxy(Node node, RelationshipManager relationshipManager, InstanceManager instanceManager) {
        this.node = node;
        this.relationshipManager = relationshipManager;
        this.instanceManager = instanceManager;
    }

    public Iterator<E> iterator() {
        final Iterator<Node> iterator = relationshipManager.getRelationships(node);
        return new Iterator<E>() {

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public E next() {
                return instanceManager.getInstance(iterator.next());
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Remove not supported");
            }
        };
    }

    public int size() {
        int size = 0;
        for (Iterator<Node> iterator = relationshipManager.getRelationships(node); iterator.hasNext(); ) {
            iterator.next();
            size++;
        }
        return size;
    }

    @Override
    public boolean add(E e) {
        Node target = instanceManager.getEntity(e);
        relationshipManager.createRelationship(node, target);
        return true;
    }

    @Override
    public boolean remove(Object o) {
        if (instanceManager.isEntity(o)) {
            Node target = instanceManager.getEntity(o);
            return relationshipManager.removeRelationship(node, target);
        }
        return false;
    }
}
