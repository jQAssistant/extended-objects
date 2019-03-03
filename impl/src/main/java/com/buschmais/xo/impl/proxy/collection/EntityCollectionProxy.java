package com.buschmais.xo.impl.proxy.collection;

import java.util.Collection;
import java.util.Iterator;

import com.buschmais.xo.impl.SessionContext;
import com.buschmais.xo.spi.metadata.method.EntityCollectionPropertyMethodMetadata;
import com.buschmais.xo.spi.session.InstanceManager;

public class EntityCollectionProxy<Instance, Entity, Relation>
        extends AbstractCollectionProxy<Instance, Entity, Relation, EntityCollectionPropertyMethodMetadata<?>> implements Collection<Instance> {

    public EntityCollectionProxy(SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?, ?> sessionContext, Entity entity,
            EntityCollectionPropertyMethodMetadata<?> metadata) {
        super(sessionContext, entity, metadata);
    }

    public Iterator<Instance> iterator() {
        final SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?, ?> sessionContext = getSessionContext();
        final Iterator<Entity> iterator = sessionContext.getEntityPropertyManager().getEntityCollection(getEntity(), getMetadata());
        return sessionContext.getInterceptorFactory().addInterceptor(new Iterator<Instance>() {

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public Instance next() {
                return sessionContext.getEntityInstanceManager().readInstance(iterator.next());
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Remove not supported");
            }
        }, Iterator.class);
    }

    @Override
    public boolean add(Instance instance) {
        getSessionContext().getEntityPropertyManager().createEntityReference(getEntity(), getMetadata(), instance);
        return true;
    }

    @Override
    public boolean remove(Object o) {
        SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?, ?> sessionContext = getSessionContext();
        InstanceManager<?, Entity> instanceManager = sessionContext.getEntityInstanceManager();
        if (instanceManager.isInstance(o)) {
            return sessionContext.getEntityPropertyManager().removeEntityReference(getEntity(), getMetadata(), o);
        }
        return false;
    }
}
