package com.buschmais.cdo.impl.proxy.entity.property;

import com.buschmais.cdo.api.proxy.ProxyMethod;
import com.buschmais.cdo.impl.InstanceManager;
import com.buschmais.cdo.impl.PropertyManager;
import com.buschmais.cdo.spi.metadata.method.AbstractMethodMetadata;

public abstract class AbstractPropertyMethod<Entity, Relation, M extends AbstractMethodMetadata> implements ProxyMethod<Entity> {

    private M metadata;
    private InstanceManager<?, Entity, ?, ?, Relation, ?> instanceManager;
    private PropertyManager<?, Entity, ?, Relation> propertyManager;

    protected AbstractPropertyMethod(M metadata, InstanceManager<?, Entity, ?, ?, Relation, ?> instanceManager, PropertyManager<?, Entity, ?, Relation> propertyManager) {
        this.metadata = metadata;
        this.instanceManager = instanceManager;
        this.propertyManager = propertyManager;
    }

    protected M getMetadata() {
        return metadata;
    }

    protected InstanceManager<?, Entity, ?, ?, Relation, ?> getInstanceManager() {
        return instanceManager;
    }

    public PropertyManager<?, Entity, ?, Relation> getPropertyManager() {
        return propertyManager;
    }
}
