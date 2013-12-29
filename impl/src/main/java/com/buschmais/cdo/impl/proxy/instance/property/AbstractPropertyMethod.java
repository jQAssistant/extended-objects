package com.buschmais.cdo.impl.proxy.instance.property;

import com.buschmais.cdo.api.proxy.ProxyMethod;
import com.buschmais.cdo.impl.InstanceManager;
import com.buschmais.cdo.impl.PropertyManager;
import com.buschmais.cdo.spi.metadata.AbstractMethodMetadata;

public abstract class AbstractPropertyMethod<Entity, M extends AbstractMethodMetadata> implements ProxyMethod<Entity> {

    private final M metadata;
    private final InstanceManager<?, Entity> instanceManager;
    private final PropertyManager<?, Entity, ?, ?> propertyManager;

    protected AbstractPropertyMethod(M metadata, InstanceManager<?, Entity> instanceManager, PropertyManager<?, Entity, ?, ?> propertyManager) {
        this.metadata = metadata;
        this.instanceManager = instanceManager;
        this.propertyManager = propertyManager;
    }

    protected M getMetadata() {
        return metadata;
    }

    protected InstanceManager<?, Entity> getInstanceManager() {
        return instanceManager;
    }

    public PropertyManager<?, Entity, ?, ?> getPropertyManager() {
        return propertyManager;
    }
}
