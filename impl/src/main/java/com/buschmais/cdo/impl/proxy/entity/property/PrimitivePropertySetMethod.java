package com.buschmais.cdo.impl.proxy.entity.property;

import com.buschmais.cdo.impl.EntityPropertyManager;
import com.buschmais.cdo.impl.proxy.common.property.AbstractPrimitivePropertySetMethod;
import com.buschmais.cdo.spi.metadata.method.PrimitivePropertyMethodMetadata;

public class PrimitivePropertySetMethod<Entity, Relation> extends AbstractPrimitivePropertySetMethod<Entity, EntityPropertyManager<Entity, Relation>> {

    public PrimitivePropertySetMethod(EntityPropertyManager<Entity, Relation> propertyManager, PrimitivePropertyMethodMetadata metadata) {
        super(propertyManager, metadata);
    }
}
