package com.buschmais.cdo.impl.proxy.relation.object;

import com.buschmais.cdo.api.proxy.ProxyMethod;
import com.buschmais.cdo.impl.SessionContext;

public class ToStringMethod<Relation> implements ProxyMethod<Relation> {

    private SessionContext<?, ?, ?, ?, ?, Relation, ?, ?> sessionContext;

    public ToStringMethod(SessionContext<?, ?, ?, ?, ?, Relation, ?, ?> sessionContext) {
        this.sessionContext = sessionContext;
    }

    @Override
    public Object invoke(Relation relation, Object instance, Object[] args) {
        StringBuffer stringBuffer = new StringBuffer();
        for (Class<?> type : instance.getClass().getInterfaces()) {
            if (stringBuffer.length() > 0) {
                stringBuffer.append('|');
            }
            stringBuffer.append(type);
        }
        stringBuffer.append(", id=");
        stringBuffer.append(sessionContext.getDatastoreSession().getRelationId(relation));
        return stringBuffer.toString();
    }
}
