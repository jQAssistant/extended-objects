package com.buschmais.cdo.impl.proxy.common.property;

import com.buschmais.cdo.impl.AbstractPropertyManager;
import com.buschmais.cdo.impl.SessionContext;
import com.buschmais.cdo.spi.metadata.method.PrimitivePropertyMethodMetadata;

public abstract class AbstractPrimitivePropertySetMethod<DatastoreType, Entity, Relation> extends AbstractPropertyMethod<DatastoreType, Entity, Relation, PrimitivePropertyMethodMetadata> {

    public AbstractPrimitivePropertySetMethod(SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> sessionContext, PrimitivePropertyMethodMetadata metadata) {
        super(sessionContext, metadata);
    }

    public Object invoke(DatastoreType datastoreType, Object instance, Object[] args) {
        Object value = args[0];
        AbstractPropertyManager<DatastoreType, Entity, Relation> propertyManager = getPropertyManager();
        PrimitivePropertyMethodMetadata<?> metadata = getMetadata();
        if (value != null) {
            if (Enum.class.isAssignableFrom(metadata.getAnnotatedMethod().getType())) {
                value = ((Enum) value).name();
            }
            propertyManager.setProperty(datastoreType, metadata, value);
        } else {
            if (propertyManager.hasProperty(datastoreType, metadata)) {
                propertyManager.removeProperty(datastoreType, metadata);
            }
        }
        return null;
    }
}
