package com.buschmais.xo.impl.proxy.relation.object;

import com.buschmais.xo.impl.SessionContext;
import com.buschmais.xo.impl.proxy.common.object.AbstractDatastoreTypeEqualsMethod;
import com.buschmais.xo.spi.session.InstanceManager;

public class EqualsMethod<Relation> extends AbstractDatastoreTypeEqualsMethod<Relation> {

    private final SessionContext<?, ?, ?, ?, ?, Relation, ?, ?, ?> sessionContext;

    public EqualsMethod(SessionContext<?, ?, ?, ?, ?, Relation, ?, ?, ?> sessionContext) {
        this.sessionContext = sessionContext;
    }

    @Override
    protected InstanceManager<?, Relation> getInstanceManager() {
        return sessionContext.getRelationInstanceManager();
    }
}
