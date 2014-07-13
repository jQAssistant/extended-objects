package com.buschmais.xo.impl.proxy.common.property;

import com.buschmais.xo.impl.AbstractPropertyManager;
import com.buschmais.xo.spi.metadata.method.TransientPropertyMethodMetadata;

public abstract class AbstractTransientPropertySetMethod<DatastoreType, PropertyManager extends AbstractPropertyManager<DatastoreType>> extends AbstractPropertyMethod<DatastoreType, PropertyManager, TransientPropertyMethodMetadata> {

    public AbstractTransientPropertySetMethod(PropertyManager propertyManager, TransientPropertyMethodMetadata metadata) {
        super(propertyManager, metadata);
    }

    @Override
    public Object invoke(DatastoreType datastoreType, Object instance, Object[] args) {
        Object value = args[0];
        getPropertyManager().setTransientProperty(datastoreType, getMetadata(), value);
        return null;
    }
}
