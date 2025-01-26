package com.buschmais.xo.impl.proxy.relation.property;

import com.buschmais.xo.api.metadata.method.TransientPropertyMethodMetadata;
import com.buschmais.xo.impl.RelationPropertyManager;
import com.buschmais.xo.impl.proxy.common.property.AbstractTransientPropertySetMethod;

public class TransientPropertySetMethod<Entity, Relation>
    extends AbstractTransientPropertySetMethod<Entity, Relation, Relation, RelationPropertyManager<Entity, Relation>> {

    public TransientPropertySetMethod(RelationPropertyManager<Entity, Relation> propertyManager, TransientPropertyMethodMetadata metadata) {
        super(propertyManager, metadata);
    }
}
