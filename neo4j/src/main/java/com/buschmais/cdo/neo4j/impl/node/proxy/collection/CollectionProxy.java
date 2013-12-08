package com.buschmais.cdo.neo4j.impl.node.proxy.collection;

import com.buschmais.cdo.neo4j.impl.common.InstanceManager;
import com.buschmais.cdo.neo4j.impl.node.metadata.RelationshipMetadata;
import com.buschmais.cdo.neo4j.impl.common.PropertyManager;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import java.util.AbstractCollection;
import java.util.Iterator;

public class CollectionProxy<E> extends AbstractCollection<E> {

    private Node node;
    private RelationshipMetadata metadata;
    private RelationshipMetadata.Direction direction;
    private InstanceManager<Long, Node> instanceManager;
    private PropertyManager<Long, Node, Long, Relationship> propertyManager;

    public CollectionProxy(Node node, RelationshipMetadata metadata, RelationshipMetadata.Direction direction, InstanceManager instanceManager, PropertyManager propertyManager) {
        this.node = node;
        this.metadata = metadata;
        this.direction = direction;
        this.instanceManager = instanceManager;
        this.propertyManager = propertyManager;
    }

    public Iterator<E> iterator() {
        final Iterator<Node> iterator = propertyManager.getRelations(node, metadata, direction);
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
        for (Iterator<Node> iterator = propertyManager.getRelations(node, metadata, direction); iterator.hasNext(); ) {
            iterator.next();
            size++;
        }
        return size;
    }

    @Override
    public boolean add(E e) {
        Node target = instanceManager.getEntity(e);
        propertyManager.createRelation(node, metadata, direction, target);
        return true;
    }

    @Override
    public boolean remove(Object o) {
        if (instanceManager.isEntity(o)) {
            Node target = instanceManager.getEntity(o);
            return propertyManager.removeRelation(node, metadata, direction, target);
        }
        return false;
    }
}
