package com.buschmais.xo.impl.proxy.common.property;

import com.buschmais.xo.impl.AbstractPropertyManager;
import com.buschmais.xo.spi.metadata.method.PrimitivePropertyMethodMetadata;

public abstract class AbstractPrimitivePropertySetMethod<DatastoreType, PropertyManager extends AbstractPropertyManager<DatastoreType>>
        extends AbstractPropertyMethod<DatastoreType, PropertyManager, PrimitivePropertyMethodMetadata> {

    public AbstractPrimitivePropertySetMethod(PropertyManager propertyManager, PrimitivePropertyMethodMetadata metadata) {
        super(propertyManager, metadata);
    }

    public Object invoke(DatastoreType datastoreType, Object instance, Object[] args) {
        Object value = args[0];
        PropertyManager propertyManager = getPropertyManager();
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
