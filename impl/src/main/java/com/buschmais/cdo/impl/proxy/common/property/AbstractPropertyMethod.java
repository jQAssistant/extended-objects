package com.buschmais.cdo.impl.proxy.common.property;

import com.buschmais.cdo.api.proxy.ProxyMethod;
import com.buschmais.cdo.impl.AbstractPropertyManager;
import com.buschmais.cdo.spi.metadata.method.AbstractMethodMetadata;

public abstract class AbstractPropertyMethod<DatastoreType, PropertyManager extends AbstractPropertyManager<DatastoreType, ?, ?>, M extends AbstractMethodMetadata> implements ProxyMethod<DatastoreType> {

    private M metadata;

    private PropertyManager propertyManager;

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
