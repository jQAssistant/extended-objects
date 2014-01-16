package com.buschmais.cdo.impl.proxy.relation.object;

import com.buschmais.cdo.api.proxy.ProxyMethod;
import com.buschmais.cdo.impl.SessionContext;

public class HashCodeMethod<Relation> implements ProxyMethod<Relation> {

    private SessionContext<?, ?, ?, ?, ?, Relation, ?, ?> sessionContext;

    public HashCodeMethod(SessionContext<?, ?, ?, ?, ?, Relation, ?, ?> sessionContext) {
        this.sessionContext = sessionContext;
    }

    @Override
    public Object invoke(Relation relation, Object instance, Object[] args) {
        return sessionContext.getDatastoreSession().getRelationId(relation).hashCode();
    }
}
