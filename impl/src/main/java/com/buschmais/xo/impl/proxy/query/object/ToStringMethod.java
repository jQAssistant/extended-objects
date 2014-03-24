package com.buschmais.xo.impl.proxy.query.object;

import com.buschmais.xo.impl.proxy.common.object.AbstractToStringMethod;
import com.buschmais.xo.impl.proxy.query.RowProxyMethod;

import java.util.Map;

public class ToStringMethod extends AbstractToStringMethod<Map<String, Object>> implements RowProxyMethod {

    @Override
    protected String getId(Map<String, Object> datastoreType) {
        return datastoreType.toString();
    }

}
