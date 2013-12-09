package com.buschmais.cdo.neo4j.impl.node.proxy.method.object;

import com.buschmais.cdo.neo4j.spi.DatastoreSession;
import com.buschmais.cdo.spi.proxy.ProxyMethod;

public class ToStringMethod<Entity> implements ProxyMethod<Entity> {

    private DatastoreSession<?, Entity, ?, ?, ?, ?, ?> datastoreSession;

    public ToStringMethod(DatastoreSession datastoreSession) {
        this.datastoreSession = datastoreSession;
    }

    @Override
    public Object invoke(Entity entity, Object instance, Object[] args) {
        StringBuffer stringBuffer = new StringBuffer();
        for (Class<?> type : instance.getClass().getInterfaces()) {
            if (stringBuffer.length() > 0) {
                stringBuffer.append('|');
            }
            stringBuffer.append(type);
        }
        stringBuffer.append(", id=");
        stringBuffer.append(datastoreSession.getId(entity));
        return stringBuffer.toString();
    }
}
