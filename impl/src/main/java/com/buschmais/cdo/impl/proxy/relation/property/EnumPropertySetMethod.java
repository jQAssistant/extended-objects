package com.buschmais.cdo.impl.proxy.relation.property;

import com.buschmais.cdo.impl.AbstractPropertyManager;
import com.buschmais.cdo.impl.SessionContext;
import com.buschmais.cdo.impl.proxy.common.property.AbstractPropertyMethod;
import com.buschmais.cdo.spi.metadata.method.EnumPropertyMethodMetadata;

public class EnumPropertySetMethod<Entity, Relation> extends AbstractPropertyMethod<Relation,Entity,Relation,EnumPropertyMethodMetadata> {

    public EnumPropertySetMethod(SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> sessionContext, EnumPropertyMethodMetadata metadata) {
        super(sessionContext, metadata);
    }

    @Override
    protected AbstractPropertyManager<Relation, Entity, Relation> getPropertyManager() {
        return getSessionContext().getRelationPropertyManager();
    }

    @Override
    public Object invoke(Relation entity, Object instance, Object[] args) {
        Enum<?> value = (Enum<?>) args[0];
        getSessionContext().getRelationPropertyManager().setEnumProperty(entity, getMetadata(), value);
        return null;
    }
}
