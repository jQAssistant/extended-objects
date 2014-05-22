package com.buschmais.xo.impl.proxy.entity.composite;

import com.buschmais.xo.impl.SessionContext;
import com.buschmais.xo.impl.proxy.common.composite.AbstractAsMethod;

public class AsMethod<Entity> extends AbstractAsMethod<Entity> {

    private final SessionContext<?, Entity, ?, ?, ?, ?, ?, ?, ?> sessionContext;

    public AsMethod(SessionContext<?, Entity, ?, ?, ?, ?, ?, ?, ?> sessionContext) {
        this.sessionContext = sessionContext;
    }

    @Override
    protected Object getInstance(Object instance, Entity entity) {
        return sessionContext.getEntityInstanceManager().readInstance(entity);
    }
}
