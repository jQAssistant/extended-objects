package com.buschmais.xo.impl.proxy.entity.object;

import com.buschmais.xo.impl.SessionContext;
import com.buschmais.xo.impl.proxy.common.object.AbstractToStringMethod;

public class ToStringMethod<Entity> extends AbstractToStringMethod<Entity> {

    private final SessionContext<?, Entity, ?, ?, ?, ?, ?, ?> sessionContext;

    public ToStringMethod(SessionContext<?, Entity, ?, ?, ?, ?, ?, ?> sessionContext) {
        this.sessionContext = sessionContext;
    }

    @Override
    protected String getId(Entity datastoreType) {
        return sessionContext.getDatastoreSession().getEntityId(datastoreType).toString();
    }
}
