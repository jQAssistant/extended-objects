package com.buschmais.xo.impl.proxy.relation.property;

import com.buschmais.xo.impl.RelationPropertyManager;
import com.buschmais.xo.impl.proxy.common.property.AbstractTransientPropertyGetMethod;
import com.buschmais.xo.api.metadata.method.TransientPropertyMethodMetadata;

public class TransientPropertyGetMethod<Entity, Relation> extends AbstractTransientPropertyGetMethod<Relation, RelationPropertyManager<Entity, Relation>> {

    public TransientPropertyGetMethod(RelationPropertyManager<Entity, Relation> propertyManager, TransientPropertyMethodMetadata metadata) {
        super(propertyManager, metadata);
    }
}
