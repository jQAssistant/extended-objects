package com.buschmais.cdo.impl.proxy.relation.property;

import com.buschmais.cdo.impl.AbstractPropertyManager;
import com.buschmais.cdo.impl.SessionContext;
import com.buschmais.cdo.impl.proxy.common.property.AbstractPrimitivePropertyGetMethod;
import com.buschmais.cdo.spi.metadata.method.PrimitivePropertyMethodMetadata;

public class PrimitivePropertyGetMethod<Entity, Relation> extends AbstractPrimitivePropertyGetMethod<Relation, Entity, Relation> {

    public PrimitivePropertyGetMethod(SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> sessionContext, PrimitivePropertyMethodMetadata metadata) {
        super(sessionContext, metadata);
    }

    @Override
    protected AbstractPropertyManager<Relation, Entity, Relation> getPropertyManager() {
        return getSessionContext().getRelationPropertyManager();
    }
}
