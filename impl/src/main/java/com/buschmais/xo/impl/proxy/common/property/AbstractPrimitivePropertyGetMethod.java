package com.buschmais.xo.impl.proxy.common.property;

import com.buschmais.xo.api.metadata.method.PrimitivePropertyMethodMetadata;
import com.buschmais.xo.impl.AbstractPropertyManager;
import com.buschmais.xo.impl.converter.ValueConverter;

public abstract class AbstractPrimitivePropertyGetMethod<DatastoreType, PropertyManager extends AbstractPropertyManager<DatastoreType>>
    extends AbstractPropertyMethod<DatastoreType, PropertyManager, PrimitivePropertyMethodMetadata> {

    public AbstractPrimitivePropertyGetMethod(PropertyManager propertyManager, PrimitivePropertyMethodMetadata metadata) {
        super(propertyManager, metadata);
    }

    @Override
    public Object invoke(DatastoreType datastoreType, Object instance, Object[] args) {
        PrimitivePropertyMethodMetadata<?> metadata = getMetadata();
        PropertyManager propertyManager = getPropertyManager();
        if (!propertyManager.hasProperty(datastoreType, metadata)) {
            return null;
        }
        Object value = propertyManager.getProperty(datastoreType, metadata);
        return ValueConverter.convert(value, metadata.getAnnotatedMethod().getType());
    }

}
