package com.buschmais.cdo.impl.proxy.entity.property;

import com.buschmais.cdo.impl.SessionContext;
import com.buschmais.cdo.spi.metadata.method.EnumPropertyMethodMetadata;

public class EnumPropertyGetMethod<Entity, Relation> extends AbstractPropertyMethod<Entity, Relation, EnumPropertyMethodMetadata> {

    public EnumPropertyGetMethod(SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> sessionContext, EnumPropertyMethodMetadata metadata) {
        super(sessionContext, metadata);
    }

    @Override
    public Object invoke(Entity entity, Object instance, Object[] args) {
        return getSessionContext().getPropertyManager().getEnumProperty(entity, getMetadata());
    }
}
