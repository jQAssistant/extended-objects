package com.buschmais.xo.impl.proxy.entity.composite;

import com.buschmais.xo.api.proxy.ProxyMethod;
import com.buschmais.xo.impl.SessionContext;

/**
 * Implementation of {@link com.buschmais.xo.api.CompositeObject#getId()}.
 */
public class GetIdMethod<Entity> implements ProxyMethod<Entity> {

    private final SessionContext<?, Entity, ?, ?, ?, ?, ?, ?, ?> sessionContext;

    public GetIdMethod(SessionContext<?, Entity, ?, ?, ?, ?, ?, ?, ?> sessionContext) {
        this.sessionContext = sessionContext;
    }

    @Override
    public Object invoke(Entity entity, Object instance, Object[] args) {
        return sessionContext.getDatastoreSession()
            .getDatastoreEntityManager()
            .getEntityId(entity);
    }
}
