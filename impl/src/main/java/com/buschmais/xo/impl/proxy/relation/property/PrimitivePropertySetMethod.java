package com.buschmais.xo.impl.proxy.relation.property;

import com.buschmais.xo.api.metadata.method.PrimitivePropertyMethodMetadata;
import com.buschmais.xo.impl.RelationPropertyManager;
import com.buschmais.xo.impl.proxy.common.property.AbstractPrimitivePropertySetMethod;

public class PrimitivePropertySetMethod<Entity, Relation>
    extends AbstractPrimitivePropertySetMethod<Entity, Relation, Relation, RelationPropertyManager<Entity, Relation>> {

    public PrimitivePropertySetMethod(RelationPropertyManager<Entity, Relation> propertyManager, PrimitivePropertyMethodMetadata metadata) {
        super(propertyManager, metadata);
    }
}
