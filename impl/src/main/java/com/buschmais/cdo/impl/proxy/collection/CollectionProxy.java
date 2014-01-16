package com.buschmais.cdo.impl.proxy.collection;

import com.buschmais.cdo.impl.InstanceManager;
import com.buschmais.cdo.impl.SessionContext;
import com.buschmais.cdo.spi.metadata.type.RelationTypeMetadata;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;

public class CollectionProxy<Instance, Entity> extends AbstractCollection<Instance> implements Collection<Instance> {

    private SessionContext<?, Entity, ?, ?, ?, ?, ?, ?> sessionContext;
    private Entity entity;
    private RelationTypeMetadata metadata;
    private RelationTypeMetadata.Direction direction;

    public CollectionProxy(SessionContext<?, Entity, ?, ?, ?, ?, ?, ?> sessionContext, Entity entity, RelationTypeMetadata metadata, RelationTypeMetadata.Direction direction) {
        this.sessionContext = sessionContext;
        this.entity = entity;
        this.metadata = metadata;
        this.direction = direction;
    }

    public Iterator<Instance> iterator() {
        final Iterator<Entity> iterator = sessionContext.getPropertyManager().getRelations(entity, metadata, direction);
        return sessionContext.getInterceptorFactory().addInterceptor(new Iterator<Instance>() {

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public Instance next() {
                return sessionContext.getEntityInstanceManager().getInstance(iterator.next());
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Remove not supported");
            }
        });
    }

    public int size() {
        int size = 0;
        for (Iterator<Entity> iterator = sessionContext.getPropertyManager().getRelations(entity, metadata, direction); iterator.hasNext(); ) {
            iterator.next();
            size++;
        }
        return size;
    }

    @Override
    public boolean add(Instance instance) {
        Entity target = sessionContext.getEntityInstanceManager().getDatastoreType(instance);
        sessionContext.getPropertyManager().createRelation(entity, metadata, direction, target);
        return true;
    }

    @Override
    public boolean remove(Object o) {
        InstanceManager<?,Entity> instanceManager = sessionContext.getEntityInstanceManager();
        if (instanceManager.isInstance(o)) {
            Entity target = instanceManager.getDatastoreType(o);
            return sessionContext.getPropertyManager().removeRelation(entity, metadata, direction, target);
        }
        return false;
    }
}
