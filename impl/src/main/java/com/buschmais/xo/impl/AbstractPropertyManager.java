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
public abstract class AbstractPropertyManager<DatastoreType, Entity, Relation> {

    private final SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?, ?> sessionContext;

    /**
     * Constructor.
     *
     * @param sessionContext The {@link com.buschmais.xo.impl.SessionContext}.
     */
    public AbstractPropertyManager(SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?, ?> sessionContext) {
        this.sessionContext = sessionContext;
    }

    protected SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?, ?> getSessionContext() {
        return sessionContext;
    }

    public void setProperty(DatastoreType datastoreType, PrimitivePropertyMethodMetadata metadata, Object value) {
        getPropertyManager().setProperty(datastoreType, metadata, value);
        getInstanceManager().updateInstance(datastoreType);
    }

    public Object getProperty(DatastoreType datastoreType, PrimitivePropertyMethodMetadata metadata) {
        return getPropertyManager().getProperty(datastoreType, metadata);
    }

    public boolean hasProperty(DatastoreType datastoreType, PrimitivePropertyMethodMetadata metadata) {
        return getPropertyManager().hasProperty(datastoreType, metadata);
    }

    public void removeProperty(DatastoreType datastoreType, PrimitivePropertyMethodMetadata metadata) {
        getPropertyManager().removeProperty(datastoreType, metadata);
        getInstanceManager().updateInstance(datastoreType);
    }

    protected abstract DatastorePropertyManager<DatastoreType, ?> getPropertyManager();

    protected abstract AbstractInstanceManager<?, DatastoreType> getInstanceManager();

}
