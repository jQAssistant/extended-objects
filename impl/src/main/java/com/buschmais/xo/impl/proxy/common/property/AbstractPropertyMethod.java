package com.buschmais.xo.impl.proxy.common.property;

import com.buschmais.xo.api.metadata.method.AbstractMethodMetadata;
import com.buschmais.xo.api.proxy.ProxyMethod;
import com.buschmais.xo.impl.AbstractPropertyManager;

public abstract class AbstractPropertyMethod<Entity, Relation, DatastoreType, PropertyManager extends AbstractPropertyManager<Entity, Relation, DatastoreType>, M extends AbstractMethodMetadata>
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
