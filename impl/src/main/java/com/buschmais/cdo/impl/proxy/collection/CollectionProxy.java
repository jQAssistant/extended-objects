package com.buschmais.cdo.impl.proxy.collection;

import com.buschmais.cdo.impl.AbstractInstanceManager;
import com.buschmais.cdo.impl.SessionContext;
import com.buschmais.cdo.spi.metadata.method.AbstractRelationPropertyMethodMetadata;
import com.buschmais.cdo.spi.metadata.method.CollectionPropertyMethodMetadata;
import com.buschmais.cdo.spi.metadata.type.RelationTypeMetadata;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;

public class CollectionProxy<Instance, Entity> extends AbstractCollection<Instance> implements Collection<Instance> {

    private SessionContext<?, Entity, ?, ?, ?, ?, ?, ?> sessionContext;
    private Entity entity;
    private CollectionPropertyMethodMetadata<?> metadata;

    public CollectionProxy(SessionContext<?, Entity, ?, ?, ?, ?, ?, ?> sessionContext, Entity entity, CollectionPropertyMethodMetadata<?> metadata) {
        this.sessionContext = sessionContext;
        this.entity = entity;
        this.metadata = metadata;
    }

    public Iterator<Instance> iterator() {
        final Iterator<Entity> iterator = sessionContext.getEntityPropertyManager().getEntityCollection(entity, metadata);
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
        for (Iterator<Entity> iterator = sessionContext.getEntityPropertyManager().getEntityCollection(entity, metadata); iterator.hasNext(); ) {
            iterator.next();
            size++;
        }
        return size;
    }

    @Override
    public boolean add(Instance instance) {
        sessionContext.getEntityPropertyManager().createEntityReference(entity, metadata, instance);
        return true;
    }

    @Override
    public boolean remove(Object o) {
        AbstractInstanceManager<?,Entity> instanceManager = sessionContext.getEntityInstanceManager();
        if (instanceManager.isInstance(o)) {
            return sessionContext.getEntityPropertyManager().removeEntityReference(entity, metadata, o);
        }
        return false;
    }
}
