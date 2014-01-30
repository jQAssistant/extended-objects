package com.buschmais.cdo.impl.proxy.collection;

import com.buschmais.cdo.impl.AbstractInstanceManager;
import com.buschmais.cdo.impl.SessionContext;
import com.buschmais.cdo.spi.metadata.method.EntityCollectionPropertyMethodMetadata;

import java.util.Collection;
import java.util.Iterator;

public class EntityCollectionProxy<Instance, Entity, Relation> extends AbstractCollectionProxy<Instance, Entity, Relation, EntityCollectionPropertyMethodMetadata<?>> implements Collection<Instance> {

    public EntityCollectionProxy(SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> sessionContext, Entity entity, EntityCollectionPropertyMethodMetadata<?> metadata) {
        super(sessionContext, entity, metadata);
    }

    public Iterator<Instance> iterator() {
        final SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> sessionContext = getSessionContext();
        final Iterator<Entity> iterator = sessionContext.getEntityPropertyManager().getEntityCollection(getEntity(), getMetadata());
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

    @Override
    public boolean add(Instance instance) {
        getSessionContext().getEntityPropertyManager().createEntityReference(getEntity(), getMetadata(), instance);
        return true;
    }

    @Override
    public boolean remove(Object o) {
        SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> sessionContext = getSessionContext();
        AbstractInstanceManager<?, Entity> instanceManager = sessionContext.getEntityInstanceManager();
        if (instanceManager.isInstance(o)) {
            return sessionContext.getEntityPropertyManager().removeEntityReference(getEntity(), getMetadata(), o);
        }
        return false;
    }
}
