package com.buschmais.xo.impl.proxy.relation.property;

import com.buschmais.xo.impl.RelationPropertyManager;
import com.buschmais.xo.impl.proxy.common.property.AbstractTransientPropertySetMethod;
import com.buschmais.xo.api.metadata.method.TransientPropertyMethodMetadata;

public class TransientPropertySetMethod<Entity, Relation> extends AbstractTransientPropertySetMethod<Relation, RelationPropertyManager<Entity, Relation>> {

    public TransientPropertySetMethod(RelationPropertyManager<Entity, Relation> propertyManager, TransientPropertyMethodMetadata metadata) {
        super(propertyManager, metadata);
    }
}
