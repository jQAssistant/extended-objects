package com.buschmais.cdo.impl.proxy.relation.property;

import com.buschmais.cdo.impl.RelationPropertyManager;
import com.buschmais.cdo.impl.proxy.common.property.AbstractPrimitivePropertyGetMethod;
import com.buschmais.cdo.spi.metadata.method.PrimitivePropertyMethodMetadata;

public class PrimitivePropertyGetMethod<Entity, Relation> extends AbstractPrimitivePropertyGetMethod<Relation, RelationPropertyManager<Entity, Relation>> {

    public PrimitivePropertyGetMethod(RelationPropertyManager<Entity, Relation> propertyManager, PrimitivePropertyMethodMetadata metadata) {
        super(propertyManager, metadata);
    }
}
