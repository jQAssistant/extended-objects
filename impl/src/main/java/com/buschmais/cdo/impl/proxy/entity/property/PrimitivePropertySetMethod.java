package com.buschmais.cdo.impl.proxy.entity.property;

import com.buschmais.cdo.impl.PropertyManager;
import com.buschmais.cdo.impl.SessionContext;
import com.buschmais.cdo.spi.metadata.method.PrimitivePropertyMethodMetadata;

public class PrimitivePropertySetMethod<Entity, Relation> extends AbstractPropertyMethod<Entity, Relation, PrimitivePropertyMethodMetadata> {

    public PrimitivePropertySetMethod(SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> sessionContext, PrimitivePropertyMethodMetadata metadata) {
        super(sessionContext, metadata);
    }

    public Object invoke(Entity entity, Object instance, Object[] args) {
        Object value = args[0];
        PropertyManager<?, Entity, ?, Relation> propertyManager = getSessionContext().getPropertyManager();
        PrimitivePropertyMethodMetadata<?> metadata = getMetadata();
        if (value != null) {
            if (Enum.class.isAssignableFrom(metadata.getAnnotatedMethod().getType())) {
                value = ((Enum) value).name();
            }
            propertyManager.setProperty(entity, metadata, value);
        } else {
            if (propertyManager.hasProperty(entity, metadata)) {
                propertyManager.removeProperty(entity, metadata);
            }
        }
        return null;
    }
}
