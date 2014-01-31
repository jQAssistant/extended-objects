package com.buschmais.cdo.impl.proxy.entity.object;

import com.buschmais.cdo.impl.SessionContext;
import com.buschmais.cdo.impl.proxy.common.object.AbstractToStringMethod;

public class ToStringMethod<Entity> extends AbstractToStringMethod<Entity> {

    private SessionContext<?, Entity, ?, ?, ?, ?, ?, ?> sessionContext;

    public ToStringMethod(SessionContext<?, Entity, ?, ?, ?, ?, ?, ?> sessionContext) {
        this.sessionContext = sessionContext;
    }

    @Override
    protected String getId(Entity datastoreType) {
        return sessionContext.getDatastoreSession().getId(datastoreType).toString();
    }
}
