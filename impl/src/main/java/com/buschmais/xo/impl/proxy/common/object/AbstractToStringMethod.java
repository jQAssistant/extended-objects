package com.buschmais.xo.impl.proxy.common.object;

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
        stringBuffer.append(", id=");
        stringBuffer.append(getId(datastoreType));
        return stringBuffer.toString();
    }

    protected abstract String getId(T datastoreType);

}
