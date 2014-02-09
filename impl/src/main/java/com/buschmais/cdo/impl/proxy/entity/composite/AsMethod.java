package com.buschmais.cdo.impl.proxy.entity.composite;

import com.buschmais.cdo.impl.SessionContext;
import com.buschmais.cdo.impl.proxy.common.composite.AbstractAsMethod;

public class AsMethod<Entity> extends AbstractAsMethod<Entity> {

    private SessionContext<?, Entity, ?, ?, ?, ?, ?, ?> sessionContext;

    public AsMethod(SessionContext<?, Entity, ?, ?, ?, ?, ?, ?> sessionContext) {
        this.sessionContext = sessionContext;
    }

    @Override
    protected Object getInstance(Entity entity) {
        return sessionContext.getEntityInstanceManager().readInstance(entity);
    }
}
