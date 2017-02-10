package com.buschmais.xo.neo4j.remote.impl;

import com.buschmais.xo.neo4j.remote.api.AbstractRemotePropertyContainer;
import com.buschmais.xo.neo4j.spi.metadata.PropertyMetadata;
import com.buschmais.xo.spi.datastore.DatastorePropertyManager;
import com.buschmais.xo.spi.metadata.method.PrimitivePropertyMethodMetadata;
import org.neo4j.driver.v1.Session;

public class RemoteDatastorePropertyManager<T extends AbstractRemotePropertyContainer> implements DatastorePropertyManager<T, PropertyMetadata> {

    protected Session session;

    public RemoteDatastorePropertyManager(Session session) {
        this.session = session;
    }

    @Override
    public void setProperty(T entity, PrimitivePropertyMethodMetadata<PropertyMetadata> metadata, Object value) {
    }

    @Override
    public boolean hasProperty(T entity, PrimitivePropertyMethodMetadata<PropertyMetadata> metadata) {
        return false;
    }

    @Override
    public void removeProperty(T entity, PrimitivePropertyMethodMetadata<PropertyMetadata> metadata) {

    }

    @Override
    public Object getProperty(T entity, PrimitivePropertyMethodMetadata<PropertyMetadata> metadata) {
        return null;
    }
}
