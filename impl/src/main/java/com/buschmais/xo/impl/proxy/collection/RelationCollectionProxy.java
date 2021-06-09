package com.buschmais.xo.impl.proxy.collection;

import java.util.Collection;
import java.util.Iterator;

import com.buschmais.xo.impl.SessionContext;
import com.buschmais.xo.api.metadata.method.RelationCollectionPropertyMethodMetadata;

public class RelationCollectionProxy<Instance, Entity, Relation>
        extends AbstractCollectionProxy<Instance, Entity, Relation, RelationCollectionPropertyMethodMetadata<?>> implements Collection<Instance> {

    public RelationCollectionProxy(SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?, ?> sessionContext, Entity entity,
            RelationCollectionPropertyMethodMetadata<?> metadata) {
        super(sessionContext, entity, metadata);
    }

    @Override
    public Iterator<Instance> iterator() {
        final SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?, ?> sessionContext = getSessionContext();
        final Iterator<Relation> iterator = sessionContext.getEntityPropertyManager().getRelationCollection(getEntity(), getMetadata());
        return sessionContext.getInterceptorFactory().addInterceptor(new Iterator<Instance>() {

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public Instance next() {
                return sessionContext.getRelationInstanceManager().readInstance(iterator.next());
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Remove not supported");
            }
        }, Iterator.class);
    }

    @Override
    public boolean add(Instance instance) {
        throw new UnsupportedOperationException("Add not supported");
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("Remove not supported");
    }
}
