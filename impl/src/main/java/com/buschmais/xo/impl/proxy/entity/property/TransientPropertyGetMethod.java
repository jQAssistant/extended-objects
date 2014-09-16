package com.buschmais.xo.impl.proxy.entity.property;

import com.buschmais.xo.impl.EntityPropertyManager;
import com.buschmais.xo.impl.proxy.common.property.AbstractTransientPropertyGetMethod;
import com.buschmais.xo.spi.metadata.method.TransientPropertyMethodMetadata;

public class TransientPropertyGetMethod<Entity, Relation> extends AbstractTransientPropertyGetMethod<Entity, EntityPropertyManager<Entity, Relation, ?>> {

    public TransientPropertyGetMethod(EntityPropertyManager<Entity, Relation, ?> propertyManager, TransientPropertyMethodMetadata metadata) {
        super(propertyManager, metadata);
    }
}
