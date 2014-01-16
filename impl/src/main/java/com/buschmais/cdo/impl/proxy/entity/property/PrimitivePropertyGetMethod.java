package com.buschmais.cdo.impl.proxy.entity.property;

import com.buschmais.cdo.impl.SessionContext;
import com.buschmais.cdo.spi.metadata.method.PrimitivePropertyMethodMetadata;

public class PrimitivePropertyGetMethod<Entity, Relation> extends AbstractPropertyMethod<Entity, Relation,
        PrimitivePropertyMethodMetadata> {

    public PrimitivePropertyGetMethod(SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> sessionContext, PrimitivePropertyMethodMetadata metadata) {
        super(sessionContext, metadata);
    }

    public Object invoke(Entity entity, Object instance, Object[] args) {
        PrimitivePropertyMethodMetadata<?> metadata = getMetadata();
        if (!getSessionContext().getPropertyManager().hasProperty(entity, metadata)) {
            return null;
        }

        Object value = getSessionContext().getPropertyManager().getProperty(entity, metadata);
        Class<?> type = metadata.getAnnotatedMethod().getType();
        if (Enum.class.isAssignableFrom(type)) {
            return Enum.valueOf((Class<Enum>) type, (String) value);
        }
        return value;
    }
}
