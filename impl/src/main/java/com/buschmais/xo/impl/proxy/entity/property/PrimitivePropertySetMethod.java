package com.buschmais.xo.impl.proxy.entity.property;

import com.buschmais.xo.impl.EntityPropertyManager;
import com.buschmais.xo.impl.proxy.common.property.AbstractPrimitivePropertySetMethod;
import com.buschmais.xo.api.metadata.method.PrimitivePropertyMethodMetadata;

public class PrimitivePropertySetMethod<Entity, Relation> extends AbstractPrimitivePropertySetMethod<Entity, EntityPropertyManager<Entity, Relation, ?>> {

    public PrimitivePropertySetMethod(EntityPropertyManager<Entity, Relation, ?> propertyManager, PrimitivePropertyMethodMetadata metadata) {
        super(propertyManager, metadata);
    }
}
