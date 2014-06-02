package com.buschmais.xo.impl.proxy.entity.property;

import com.buschmais.xo.impl.EntityPropertyManager;
import com.buschmais.xo.impl.proxy.common.property.AbstractPrimitivePropertySetMethod;
import com.buschmais.xo.impl.proxy.common.property.AbstractTransientPropertySetMethod;
import com.buschmais.xo.spi.metadata.method.PrimitivePropertyMethodMetadata;
import com.buschmais.xo.spi.metadata.method.TransientPropertyMethodMetadata;

public class TransientPropertySetMethod<Entity, Relation> extends AbstractTransientPropertySetMethod<Entity, EntityPropertyManager<Entity, Relation>> {

    public TransientPropertySetMethod(EntityPropertyManager<Entity, Relation> propertyManager, TransientPropertyMethodMetadata metadata) {
        super(propertyManager, metadata);
    }
}
