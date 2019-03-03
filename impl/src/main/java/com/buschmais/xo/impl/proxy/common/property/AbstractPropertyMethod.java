package com.buschmais.xo.impl.proxy.common.property;

import com.buschmais.xo.api.proxy.ProxyMethod;
import com.buschmais.xo.impl.AbstractPropertyManager;
import com.buschmais.xo.spi.metadata.method.AbstractMethodMetadata;

public abstract class AbstractPropertyMethod<DatastoreType, PropertyManager extends AbstractPropertyManager<DatastoreType>, M extends AbstractMethodMetadata>
        implements ProxyMethod<DatastoreType> {

    private final M metadata;

    private final PropertyManager propertyManager;

    protected AbstractPropertyMethod(PropertyManager propertyManager, M metadata) {
        this.propertyManager = propertyManager;
        this.metadata = metadata;
    }

    protected M getMetadata() {
        return metadata;
    }

    public PropertyManager getPropertyManager() {
        return propertyManager;
    }
}
