package com.buschmais.xo.impl.proxy.entity.object;

import com.buschmais.xo.api.proxy.ProxyMethod;
import com.buschmais.xo.impl.SessionContext;

public class HashCodeMethod<Entity> implements ProxyMethod<Entity> {

    private final SessionContext<?, Entity, ?, ?, ?, ?, ?, ?, ?> sessionContext;

    public HashCodeMethod(SessionContext<?, Entity, ?, ?, ?, ?, ?, ?, ?> sessionContext) {
        this.sessionContext = sessionContext;
    }

    @Override
    public Object invoke(Entity entity, Object instance, Object[] args) {
        return sessionContext.getDatastoreSession().getDatastoreEntityManager().getEntityId(entity).hashCode();
    }
}
