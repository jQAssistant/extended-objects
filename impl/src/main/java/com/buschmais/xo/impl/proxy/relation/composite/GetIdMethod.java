package com.buschmais.xo.impl.proxy.relation.composite;

import com.buschmais.xo.api.proxy.ProxyMethod;
import com.buschmais.xo.impl.SessionContext;

/**
 * Implementation of {@link com.buschmais.xo.api.CompositeObject#getId()}.
 */
public class GetIdMethod<Relation> implements ProxyMethod<Relation> {

    private final SessionContext<?, ?, ?, ?, ?, Relation, ?, ?, ?> sessionContext;

    public GetIdMethod(SessionContext<?, ?, ?, ?, ?, Relation, ?, ?, ?> sessionContext) {
        this.sessionContext = sessionContext;
    }

    @Override
    public Object invoke(Relation relation, Object instance, Object[] args) throws Exception {
        return sessionContext.getDatastoreSession().getDatastoreRelationManager().getRelationId(relation);
    }
}
