package com.buschmais.xo.impl;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

import com.buschmais.xo.spi.datastore.DatastorePropertyManager;
import com.buschmais.xo.spi.metadata.method.PrimitivePropertyMethodMetadata;
import com.buschmais.xo.spi.metadata.method.TransientPropertyMethodMetadata;

public abstract class AbstractPropertyManager<DatastoreType> {

    private Map<DatastoreType, Map<String, Object>> transientInstances = null;

    public void setProperty(DatastoreType datastoreType, PrimitivePropertyMethodMetadata metadata, Object value) {
        getDatastorePropertyManager().setProperty(datastoreType, metadata, value);
        getInstanceManager().updateInstance(datastoreType);
    }

    public Object getProperty(DatastoreType datastoreType, PrimitivePropertyMethodMetadata metadata) {
        return getDatastorePropertyManager().getProperty(datastoreType, metadata);
    }

    public boolean hasProperty(DatastoreType datastoreType, PrimitivePropertyMethodMetadata metadata) {
        return getDatastorePropertyManager().hasProperty(datastoreType, metadata);
    }

    public void removeProperty(DatastoreType datastoreType, PrimitivePropertyMethodMetadata metadata) {
        getDatastorePropertyManager().removeProperty(datastoreType, metadata);
        getInstanceManager().updateInstance(datastoreType);
    }

    public void setTransientProperty(DatastoreType datastoreType, TransientPropertyMethodMetadata metadata, Object value) {
        getTransientProperties(datastoreType).put(metadata.getAnnotatedMethod().getName(), value);
    }

    public Object getTransientProperty(DatastoreType datastoreType, TransientPropertyMethodMetadata metadata) {
        return getTransientProperties(datastoreType).get(metadata.getAnnotatedMethod().getName());
    }

    protected abstract DatastorePropertyManager<DatastoreType, ?> getDatastorePropertyManager();

    protected abstract AbstractInstanceManager<?, DatastoreType> getInstanceManager();

    private Map<String, Object> getTransientProperties(DatastoreType datastoreType) {
        if (this.transientInstances == null) {
            this.transientInstances = new IdentityHashMap<>();
        }
        return this.transientInstances.computeIfAbsent(datastoreType, k -> new HashMap<>());
    }

}
