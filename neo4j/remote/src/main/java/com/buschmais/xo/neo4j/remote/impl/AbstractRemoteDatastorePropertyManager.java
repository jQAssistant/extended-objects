package com.buschmais.xo.neo4j.remote.impl;

import org.neo4j.driver.v1.Session;

import com.buschmais.xo.neo4j.remote.api.AbstractRemotePropertyContainer;
import com.buschmais.xo.neo4j.spi.metadata.PropertyMetadata;
import com.buschmais.xo.spi.datastore.DatastorePropertyManager;
import com.buschmais.xo.spi.metadata.method.PrimitivePropertyMethodMetadata;

public abstract class AbstractRemoteDatastorePropertyManager<T extends AbstractRemotePropertyContainer>
        implements DatastorePropertyManager<T, PropertyMetadata> {

    protected Session session;

    public AbstractRemoteDatastorePropertyManager(Session session) {
        this.session = session;
    }

    @Override
    public void setProperty(T entity, PrimitivePropertyMethodMetadata<PropertyMetadata> metadata, Object value) {
        entity.setProperty(metadata.getDatastoreMetadata().getName(), value);
    }

    @Override
    public boolean hasProperty(T entity, PrimitivePropertyMethodMetadata<PropertyMetadata> metadata) {
        return entity.hasProperty(metadata.getDatastoreMetadata().getName());
    }

    @Override
    public void removeProperty(T entity, PrimitivePropertyMethodMetadata<PropertyMetadata> metadata) {
        entity.removeProperty(metadata.getDatastoreMetadata().getName());
    }

    @Override
    public Object getProperty(T entity, PrimitivePropertyMethodMetadata<PropertyMetadata> metadata) {
        return entity.getProperty(metadata.getDatastoreMetadata().getName());
    }
}
