package com.buschmais.cdo.impl.proxy.collection;

import com.buschmais.cdo.impl.InstanceManager;
import com.buschmais.cdo.impl.PropertyManager;
import com.buschmais.cdo.spi.metadata.RelationMetadata;

import java.util.AbstractCollection;
import java.util.Iterator;

public class CollectionProxy<Instance, Entity> extends AbstractCollection<Instance> {

    private Entity entity;
    private RelationMetadata metadata;
    private RelationMetadata.Direction direction;
    private InstanceManager<?, Entity> instanceManager;
    private PropertyManager<?, Entity, ?, ?> propertyManager;

    public CollectionProxy(Entity entity, RelationMetadata metadata, RelationMetadata.Direction direction, InstanceManager instanceManager, PropertyManager propertyManager) {
        this.entity = entity;
        this.metadata = metadata;
        this.direction = direction;
        this.instanceManager = instanceManager;
        this.propertyManager = propertyManager;
    }

    public Iterator<Instance> iterator() {
        final Iterator<Entity> iterator = propertyManager.getRelations(entity, metadata, direction);
        return new Iterator<Instance>() {

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public Instance next() {
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
        for (Iterator<Entity> iterator = propertyManager.getRelations(entity, metadata, direction); iterator.hasNext(); ) {
            iterator.next();
            size++;
        }
        return size;
    }

    @Override
    public boolean add(Instance instance) {
        Entity target = instanceManager.getEntity(instance);
        propertyManager.createRelation(entity, metadata, direction, target);
        return true;
    }

    @Override
    public boolean remove(Object o) {
        if (instanceManager.isEntity(o)) {
            Entity target = instanceManager.getEntity(o);
            return propertyManager.removeRelation(entity, metadata, direction, target);
        }
        return false;
    }
}
