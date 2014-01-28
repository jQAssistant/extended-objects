package com.buschmais.cdo.impl.proxy.relation.property;

import com.buschmais.cdo.impl.AbstractPropertyManager;
import com.buschmais.cdo.impl.SessionContext;
import com.buschmais.cdo.impl.proxy.common.property.AbstractPrimitivePropertySetMethod;
import com.buschmais.cdo.spi.metadata.method.PrimitivePropertyMethodMetadata;

public class PrimitivePropertySetMethod<Entity, Relation> extends AbstractPrimitivePropertySetMethod<Relation,Entity, Relation> {

    public PrimitivePropertySetMethod(SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> sessionContext, PrimitivePropertyMethodMetadata metadata) {
        super(sessionContext, metadata);
    }

    @Override
    protected AbstractPropertyManager<Relation, Entity, Relation> getPropertyManager() {
        return getSessionContext().getRelationPropertyManager();
    }

}
