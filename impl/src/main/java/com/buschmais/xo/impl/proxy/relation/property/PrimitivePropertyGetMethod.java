package com.buschmais.xo.impl.proxy.relation.property;

import com.buschmais.xo.api.metadata.method.PrimitivePropertyMethodMetadata;
import com.buschmais.xo.impl.RelationPropertyManager;
import com.buschmais.xo.impl.proxy.common.property.AbstractPrimitivePropertyGetMethod;

public class PrimitivePropertyGetMethod<Entity, Relation> extends AbstractPrimitivePropertyGetMethod<Relation, RelationPropertyManager<Entity, Relation>> {

    public PrimitivePropertyGetMethod(RelationPropertyManager<Entity, Relation> propertyManager, PrimitivePropertyMethodMetadata metadata) {
        super(propertyManager, metadata);
    }
}
