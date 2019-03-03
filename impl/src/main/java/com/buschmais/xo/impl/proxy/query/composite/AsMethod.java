package com.buschmais.xo.impl.proxy.query.composite;

import java.util.Map;

import com.buschmais.xo.impl.proxy.common.composite.AbstractAsMethod;

public class AsMethod extends AbstractAsMethod<Map<String, Object>> {

    @Override
    protected Object getInstance(Object instance, Map<String, Object> row) {
        return instance;
    }
}
