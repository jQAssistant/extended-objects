package com.buschmais.cdo.impl.proxy.instance.object;

import com.buschmais.cdo.spi.datastore.DatastoreSession;
import com.buschmais.cdo.api.proxy.ProxyMethod;

public class ToStringMethod<Entity> implements ProxyMethod<Entity> {

    private final DatastoreSession datastoreSession;

    public ToStringMethod(DatastoreSession datastoreSession) {
        this.datastoreSession = datastoreSession;
    }

    @Override
    public Object invoke(Entity entity, Object instance, Object[] args) {
        StringBuilder stringBuffer = new StringBuilder();
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
