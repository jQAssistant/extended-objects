package com.buschmais.xo.neo4j.embedded.impl.model;

import java.util.HashMap;
import java.util.Map;

import com.buschmais.xo.neo4j.embedded.impl.datastore.EmbeddedNeo4jDatastoreTransaction;
import org.neo4j.graphdb.Entity;

public abstract class AbstractEmbeddedPropertyContainer<T extends Entity> implements EmbeddedNeo4jPropertyContainer {

    protected final EmbeddedNeo4jDatastoreTransaction transaction;

    protected final long id;

    public AbstractEmbeddedPropertyContainer(EmbeddedNeo4jDatastoreTransaction transaction, Entity entity) {
        this.transaction = transaction;
        this.id = entity.getId();
    }

    @Override
    public long getId() {
        return id;
    }

    public abstract T getDelegate();

    @Override
    public boolean hasProperty(String key) {
        return getDelegate().hasProperty(key);
    }

    @Override
    public Object getProperty(String key) {
        return getDelegate().getProperty(key);
    }

    @Override
    public void setProperty(String key, Object value) {
        getDelegate().setProperty(key, value);
    }

    @Override
    public Object removeProperty(String key) {
        return getDelegate().removeProperty(key);
    }

    @Override
    public Map<String, Object> getProperties() {
        Map<String, Object> properties = new HashMap<>();
        for (String key : getDelegate().getPropertyKeys()) {
            properties.put(key, getProperty(key));
        }
        return properties;
    }

    public void flush() {
    }

    public void clear() {
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof AbstractEmbeddedPropertyContainer))
            return false;

        AbstractEmbeddedPropertyContainer<?> that = (AbstractEmbeddedPropertyContainer<?>) o;

        return id == that.id;
    }

    @Override
    public final int hashCode() {
        return (int) id;
    }

    @Override
    public final String toString() {
        return getDelegate().toString();
    }
}
