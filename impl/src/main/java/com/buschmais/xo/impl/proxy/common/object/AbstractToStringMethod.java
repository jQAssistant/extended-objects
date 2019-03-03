package com.buschmais.xo.impl.proxy.common.object;

import java.util.Map;

import com.buschmais.xo.api.proxy.ProxyMethod;

public abstract class AbstractToStringMethod<T> implements ProxyMethod<T> {

    @Override
    public Object invoke(T datastoreType, Object instance, Object[] args) {
        StringBuffer stringBuffer = new StringBuffer();
        for (Class<?> type : instance.getClass().getInterfaces()) {
            if (stringBuffer.length() > 0) {
                stringBuffer.append('|');
            }
            stringBuffer.append(type.getSimpleName());
        }
        String id = getId(datastoreType);
        if (id != null) {
            stringBuffer.append(":");
            stringBuffer.append(id);
        }
        stringBuffer.append(getProperties(datastoreType));
        return stringBuffer.toString();
    }

    protected abstract Map<String, Object> getProperties(T datastoreType);

    protected abstract String getId(T datastoreType);

}
