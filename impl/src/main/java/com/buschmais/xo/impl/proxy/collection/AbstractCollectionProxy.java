package com.buschmais.xo.impl.proxy.collection;

import com.buschmais.xo.impl.SessionContext;
import com.buschmais.xo.spi.metadata.method.AbstractRelationPropertyMethodMetadata;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;

public abstract class AbstractCollectionProxy<Instance, Entity, Relation, PropertyMetadata extends AbstractRelationPropertyMethodMetadata<?>> extends AbstractCollection<Instance> implements Collection<Instance> {

    private final SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> sessionContext;
    private final Entity entity;
    private final PropertyMetadata metadata;

    public AbstractCollectionProxy(SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> sessionContext, Entity entity, PropertyMetadata metadata) {
        this.sessionContext = sessionContext;
        this.entity = entity;
        this.metadata = metadata;
    }

    public SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> getSessionContext() {
        return sessionContext;
    }

    public Entity getEntity() {
        return entity;
    }

    public PropertyMetadata getMetadata() {
        return metadata;
    }

    public int size() {
        int size = 0;
        for (Iterator<Instance> iterator = iterator(); iterator.hasNext(); ) {
            iterator.next();
            size++;
        }
        return size;
    }
}
