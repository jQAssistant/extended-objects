package com.buschmais.xo.neo4j.embedded.impl.model;

import java.util.HashMap;
import java.util.Map;

import org.neo4j.graphdb.PropertyContainer;

public abstract class AbstractEmbeddedPropertyContainer<T extends PropertyContainer> implements EmbeddedNeo4jPropertyContainer {

    private long id;

    protected T delegate;

    public AbstractEmbeddedPropertyContainer(long id, T delegate) {
        this.id = id;
        this.delegate = delegate;
    }

    @Override
    public long getId() {
        return id;
    }

    public T getDelegate() {
        return delegate;
    }

    @Override
    public boolean hasProperty(String key) {
        return delegate.hasProperty(key);
    }

    @Override
    public Object getProperty(String key) {
        return delegate.getProperty(key);
    }

    @Override
    public void setProperty(String key, Object value) {
        delegate.setProperty(key, value);
    }

    @Override
    public Object removeProperty(String key) {
        return delegate.removeProperty(key);
    }

    @Override
    public Map<String, Object> getProperties() {
        Map<String, Object> properties = new HashMap<>();
        for (String key : delegate.getPropertyKeys()) {
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
        return delegate.toString();
    }
}
