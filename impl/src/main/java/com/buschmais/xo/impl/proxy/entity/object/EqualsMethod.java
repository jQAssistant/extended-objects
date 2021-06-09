package com.buschmais.xo.impl.proxy.entity.object;

import com.buschmais.xo.impl.SessionContext;
import com.buschmais.xo.impl.proxy.common.object.AbstractDatastoreTypeEqualsMethod;
import com.buschmais.xo.spi.session.InstanceManager;

public class EqualsMethod<Entity> extends AbstractDatastoreTypeEqualsMethod<Entity> {

    private final SessionContext<?, Entity, ?, ?, ?, ?, ?, ?, ?> sessionContext;

    public EqualsMethod(SessionContext<?, Entity, ?, ?, ?, ?, ?, ?, ?> sessionContext) {
        this.sessionContext = sessionContext;
    }

    @Override
    protected InstanceManager<?, Entity> getInstanceManager() {
        return sessionContext.getEntityInstanceManager();
    }
}
