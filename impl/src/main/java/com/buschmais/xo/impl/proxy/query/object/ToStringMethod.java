package com.buschmais.xo.impl.proxy.query.object;

import java.util.Map;

import com.buschmais.xo.impl.proxy.common.object.AbstractToStringMethod;
import com.buschmais.xo.impl.proxy.query.RowProxyMethod;

public class ToStringMethod extends AbstractToStringMethod<Map<String, Object>> implements RowProxyMethod {

    @Override
    protected String getId(Map<String, Object> datastoreType) {
        return null;
    }

    @Override
    protected Map<String, Object> getProperties(Map<String, Object> datastoreType) {
        return datastoreType;
    }

}
