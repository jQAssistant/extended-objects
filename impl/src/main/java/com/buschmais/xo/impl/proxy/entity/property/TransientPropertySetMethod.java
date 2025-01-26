package com.buschmais.xo.impl.proxy.entity.property;

import com.buschmais.xo.api.metadata.method.TransientPropertyMethodMetadata;
import com.buschmais.xo.impl.EntityPropertyManager;
import com.buschmais.xo.impl.proxy.common.property.AbstractTransientPropertySetMethod;

public class TransientPropertySetMethod<Entity, Relation>
    extends AbstractTransientPropertySetMethod<Entity, Relation, Entity, EntityPropertyManager<Entity, Relation, ?>> {

    public TransientPropertySetMethod(EntityPropertyManager<Entity, Relation, ?> propertyManager, TransientPropertyMethodMetadata metadata) {
        super(propertyManager, metadata);
    }
}
