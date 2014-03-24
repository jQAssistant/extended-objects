package com.buschmais.xo.impl.proxy.relation.property;

import com.buschmais.xo.impl.RelationPropertyManager;
import com.buschmais.xo.impl.proxy.common.property.AbstractPrimitivePropertySetMethod;
import com.buschmais.xo.spi.metadata.method.PrimitivePropertyMethodMetadata;

public class PrimitivePropertySetMethod<Entity, Relation> extends AbstractPrimitivePropertySetMethod<Relation, RelationPropertyManager<Entity, Relation>> {

    public PrimitivePropertySetMethod(RelationPropertyManager<Entity, Relation> propertyManager, PrimitivePropertyMethodMetadata metadata) {
        super(propertyManager, metadata);
    }
}
