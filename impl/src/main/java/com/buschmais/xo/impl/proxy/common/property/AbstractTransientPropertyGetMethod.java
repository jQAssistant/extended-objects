package com.buschmais.xo.impl.proxy.common.property;

import com.buschmais.xo.api.metadata.method.TransientPropertyMethodMetadata;
import com.buschmais.xo.impl.AbstractPropertyManager;

public abstract class AbstractTransientPropertyGetMethod<DatastoreType, PropertyManager extends AbstractPropertyManager<DatastoreType>>
    extends AbstractPropertyMethod<DatastoreType, PropertyManager, TransientPropertyMethodMetadata> {

    public AbstractTransientPropertyGetMethod(PropertyManager propertyManager, TransientPropertyMethodMetadata metadata) {
        super(propertyManager, metadata);
    }

    @Override
    public Object invoke(DatastoreType datastoreType, Object instance, Object[] args) {
        return getPropertyManager().getTransientProperty(datastoreType, getMetadata());
    }
}
