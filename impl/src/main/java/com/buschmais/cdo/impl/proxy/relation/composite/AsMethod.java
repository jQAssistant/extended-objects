package com.buschmais.cdo.impl.proxy.relation.composite;

import com.buschmais.cdo.impl.SessionContext;
import com.buschmais.cdo.impl.proxy.common.composite.AbstractAsMethod;

public class AsMethod<Relation> extends AbstractAsMethod<Relation> {

    private SessionContext<?, ?, ?, ?, ?, Relation, ?, ?> sessionContext;

    public AsMethod(SessionContext<?, ?, ?, ?, ?, Relation, ?, ?> sessionContext) {
        this.sessionContext = sessionContext;
    }

    @Override
    protected Object getInstance(Relation relation) {
        return sessionContext.getRelationInstanceManager().getInstance(relation);
    }
}
