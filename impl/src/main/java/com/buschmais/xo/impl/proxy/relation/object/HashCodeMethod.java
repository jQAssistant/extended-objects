package com.buschmais.xo.impl.proxy.relation.object;

import com.buschmais.xo.api.proxy.ProxyMethod;
import com.buschmais.xo.impl.SessionContext;

public class HashCodeMethod<Relation> implements ProxyMethod<Relation> {

    private final SessionContext<?, ?, ?, ?, ?, Relation, ?, ?, ?> sessionContext;

    public HashCodeMethod(SessionContext<?, ?, ?, ?, ?, Relation, ?, ?, ?> sessionContext) {
        this.sessionContext = sessionContext;
    }

    @Override
    public Object invoke(Relation relation, Object instance, Object[] args) {
        return sessionContext.getDatastoreSession().getDatastoreRelationManager().getRelationId(relation).hashCode();
    }
}
