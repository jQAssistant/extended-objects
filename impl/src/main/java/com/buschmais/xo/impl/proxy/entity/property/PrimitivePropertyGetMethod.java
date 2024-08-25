package com.buschmais.xo.impl.proxy.entity.property;

import com.buschmais.xo.api.metadata.method.PrimitivePropertyMethodMetadata;
import com.buschmais.xo.impl.EntityPropertyManager;
import com.buschmais.xo.impl.proxy.common.property.AbstractPrimitivePropertyGetMethod;

public class PrimitivePropertyGetMethod<Entity, Relation> extends AbstractPrimitivePropertyGetMethod<Entity, EntityPropertyManager<Entity, Relation, ?>> {

    public PrimitivePropertyGetMethod(EntityPropertyManager<Entity, Relation, ?> propertyManager, PrimitivePropertyMethodMetadata metadata) {
        super(propertyManager, metadata);
    }
}
