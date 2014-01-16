package com.buschmais.cdo.impl.proxy.entity.property;

import com.buschmais.cdo.impl.SessionContext;
import com.buschmais.cdo.spi.metadata.method.EnumPropertyMethodMetadata;

public class EnumPropertySetMethod<Entity, Relation> extends AbstractPropertyMethod<Entity, Relation, EnumPropertyMethodMetadata> {

    public EnumPropertySetMethod(SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> sessionContext, EnumPropertyMethodMetadata metadata) {
        super(sessionContext, metadata);
    }

    @Override
    public Object invoke(Entity entity, Object instance, Object[] args) {
        Enum<?> value = (Enum<?>) args[0];
        getSessionContext().getPropertyManager().setEnumProperty(entity, getMetadata(), value);
        return null;
    }
}
