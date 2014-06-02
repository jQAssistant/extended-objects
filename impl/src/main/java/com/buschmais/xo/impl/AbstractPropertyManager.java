package com.buschmais.xo.impl;

import com.buschmais.xo.spi.datastore.DatastorePropertyManager;
import com.buschmais.xo.spi.metadata.method.PrimitivePropertyMethodMetadata;

/**
 * Contains methods for reading and creating relationships specified by the
 * given metadata.
 * <p>
 * For each provided method the direction of the relationships is handled
 * transparently for the caller.
 * </p>
 */
public abstract class AbstractPropertyManager<DatastoreType> {

    private final DatastorePropertyManager<DatastoreType, ?> datastorePropertyManager;

    private final AbstractInstanceManager<?, DatastoreType> instanceManager;

    /**
     * Constructor.
     * @param instanceManager
     * @param datastorePropertyManager
     */
    public AbstractPropertyManager(AbstractInstanceManager<?, DatastoreType> instanceManager, DatastorePropertyManager<DatastoreType, ?> datastorePropertyManager) {
        this.instanceManager = instanceManager;
        this.datastorePropertyManager = datastorePropertyManager;
    }

    public void setProperty(DatastoreType datastoreType, PrimitivePropertyMethodMetadata metadata, Object value) {
        datastorePropertyManager.setProperty(datastoreType, metadata, value);
        instanceManager.updateInstance(datastoreType);
    }

    public Object getProperty(DatastoreType datastoreType, PrimitivePropertyMethodMetadata metadata) {
        return datastorePropertyManager.getProperty(datastoreType, metadata);
    }

    public boolean hasProperty(DatastoreType datastoreType, PrimitivePropertyMethodMetadata metadata) {
        return datastorePropertyManager.hasProperty(datastoreType, metadata);
    }

    public void removeProperty(DatastoreType datastoreType, PrimitivePropertyMethodMetadata metadata) {
        datastorePropertyManager.removeProperty(datastoreType, metadata);
        instanceManager.updateInstance(datastoreType);
    }

}
