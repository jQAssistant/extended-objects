package com.buschmais.cdo.impl.proxy.collection;

import com.buschmais.cdo.impl.InstanceManager;
import com.buschmais.cdo.impl.PropertyManager;
import com.buschmais.cdo.impl.interceptor.InterceptorFactory;
import com.buschmais.cdo.spi.metadata.type.RelationTypeMetadata;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;

public class CollectionProxy<Instance, Entity> extends AbstractCollection<Instance> implements Collection<Instance> {

    private Entity entity;
    private RelationTypeMetadata metadata;
    private RelationTypeMetadata.Direction direction;
    private InstanceManager<?, Entity> instanceManager;
    private PropertyManager<?, Entity, ?, ?> propertyManager;
    private InterceptorFactory interceptorFactory;

    public CollectionProxy(Entity entity, RelationTypeMetadata metadata, RelationTypeMetadata.Direction direction, InstanceManager instanceManager, PropertyManager propertyManager, InterceptorFactory interceptorFactory) {
        this.entity = entity;
        this.metadata = metadata;
        this.direction = direction;
        this.instanceManager = instanceManager;
        this.propertyManager = propertyManager;
        this.interceptorFactory = interceptorFactory;
    }

    public Iterator<Instance> iterator() {
        final Iterator<Entity> iterator = propertyManager.getRelations(entity, metadata, direction);
        return interceptorFactory.addInterceptor(new Iterator<Instance>() {

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
        });
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
