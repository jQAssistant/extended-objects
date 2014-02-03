package com.buschmais.cdo.impl.proxy.relation.property;

import com.buschmais.cdo.impl.RelationPropertyManager;
import com.buschmais.cdo.impl.proxy.common.property.AbstractPrimitivePropertySetMethod;
import com.buschmais.cdo.spi.metadata.method.PrimitivePropertyMethodMetadata;

public class PrimitivePropertySetMethod<Entity, Relation> extends AbstractPrimitivePropertySetMethod<Relation, RelationPropertyManager<Entity, Relation>> {

    public PrimitivePropertySetMethod(RelationPropertyManager<Entity, Relation> propertyManager, PrimitivePropertyMethodMetadata metadata) {
        super(propertyManager, metadata);
    }
}
