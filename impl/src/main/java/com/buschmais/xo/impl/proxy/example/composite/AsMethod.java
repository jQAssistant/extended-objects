package com.buschmais.xo.impl.proxy.example.composite;

import java.util.Map;

import com.buschmais.xo.impl.proxy.common.composite.AbstractAsMethod;
import com.buschmais.xo.api.metadata.method.PrimitivePropertyMethodMetadata;

public class AsMethod extends AbstractAsMethod<Map<PrimitivePropertyMethodMetadata<?>, Object>> {

    @Override
    protected Object getInstance(Object instance, Map<PrimitivePropertyMethodMetadata<?>, Object> entity) {
        return instance;
    }
}
