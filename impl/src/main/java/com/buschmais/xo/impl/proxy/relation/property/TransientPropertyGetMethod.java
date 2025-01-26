package com.buschmais.xo.impl.proxy.relation.property;

import com.buschmais.xo.api.metadata.method.TransientPropertyMethodMetadata;
import com.buschmais.xo.impl.RelationPropertyManager;
import com.buschmais.xo.impl.proxy.common.property.AbstractTransientPropertyGetMethod;

public class TransientPropertyGetMethod<Entity, Relation>
    extends AbstractTransientPropertyGetMethod<Entity, Relation, Relation, RelationPropertyManager<Entity, Relation>> {

    public TransientPropertyGetMethod(RelationPropertyManager<Entity, Relation> propertyManager, TransientPropertyMethodMetadata metadata) {
        super(propertyManager, metadata);
    }
}
