package com.buschmais.xo.impl.proxy.relation.composite;

import com.buschmais.xo.impl.SessionContext;
import com.buschmais.xo.impl.proxy.common.composite.AbstractAsMethod;

public class AsMethod<Relation> extends AbstractAsMethod<Relation> {

    private final SessionContext<?, ?, ?, ?, ?, Relation, ?, ?, ?> sessionContext;

    public AsMethod(SessionContext<?, ?, ?, ?, ?, Relation, ?, ?, ?> sessionContext) {
        this.sessionContext = sessionContext;
    }

    @Override
    protected Object getInstance(Object instance, Relation relation) {
        return sessionContext.getRelationInstanceManager().readInstance(relation);
    }
}
