package com.buschmais.cdo.impl.proxy.entity.object;

import com.buschmais.cdo.impl.SessionContext;
import com.buschmais.cdo.api.proxy.ProxyMethod;

public class ToStringMethod<Entity> implements ProxyMethod<Entity> {

    private SessionContext<?, Entity, ?, ?, ?, ?, ?, ?> sessionContext;

    public ToStringMethod(SessionContext<?, Entity, ?, ?, ?, ?, ?, ?> sessionContext) {
        this.sessionContext = sessionContext;
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
        stringBuffer.append(sessionContext.getDatastoreSession().getId(entity));
        return stringBuffer.toString();
    }
}
