package com.buschmais.xo.impl.proxy.entity.property;

import com.buschmais.xo.api.metadata.method.TransientPropertyMethodMetadata;
import com.buschmais.xo.impl.EntityPropertyManager;
import com.buschmais.xo.impl.proxy.common.property.AbstractTransientPropertyGetMethod;

public class TransientPropertyGetMethod<Entity, Relation>
    extends AbstractTransientPropertyGetMethod<Entity, Relation, Entity, EntityPropertyManager<Entity, Relation, ?>> {

    public TransientPropertyGetMethod(EntityPropertyManager<Entity, Relation, ?> propertyManager, TransientPropertyMethodMetadata metadata) {
        super(propertyManager, metadata);
    }
}
