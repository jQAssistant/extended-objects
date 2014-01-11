package com.buschmais.cdo.impl.proxy.relation.object;

import com.buschmais.cdo.api.proxy.ProxyMethod;
import com.buschmais.cdo.spi.datastore.DatastoreSession;

public class ToStringMethod<Relation> implements ProxyMethod<Relation> {

    private DatastoreSession<?, ?, ?, ?, ?, Relation, ?, ?> datastoreSession;

    public ToStringMethod(DatastoreSession<?, ?, ?, ?, ?, Relation, ?, ?>  datastoreSession) {
        this.datastoreSession = datastoreSession;
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
        stringBuffer.append(datastoreSession.getRelationId(relation));
        return stringBuffer.toString();
    }
}
