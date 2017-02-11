package com.buschmais.xo.neo4j.embedded.impl.model;

import java.util.HashMap;
import java.util.Map;

import org.neo4j.graphdb.PropertyContainer;

import com.buschmais.xo.neo4j.api.model.Neo4jPropertyContainer;

public abstract class AbstractEmbeddedPropertyContainer<T extends PropertyContainer> implements Neo4jPropertyContainer {

    private long id;

    protected T delegate;

    private Map<String, Object> readCache = null;
    private Map<String, Object> writeCache = null;

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
        if (getReadCache().containsKey(key)) {
            return true;
        }
        return delegate.hasProperty(key);
    }

    @Override
    public Object getProperty(String key) {
        Map<String, Object> readCache = getReadCache();
        if (readCache.containsKey(key)) {
            return readCache.get(key);
        }
        Object value = delegate.getProperty(key);
        readCache.put(key, value);
        return value;
    };

    public void setProperty(String key, Object value) {
        getWriteCache().put(key, value);
        getReadCache().put(key, value);
    }

    public Object removeProperty(String key) {
        getWriteCache().remove(key);
        getReadCache().remove(key);
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
        if (writeCache != null) {
            for (Map.Entry<String, Object> entry : writeCache.entrySet()) {
                delegate.setProperty(entry.getKey(), entry.getValue());
            }
        }
        writeCache = null;
    }

    public void clear() {
        readCache = null;
        writeCache = null;
    }

    private Map<String, Object> getReadCache() {
        if (readCache == null) {
            readCache = new HashMap<>();
        }
        return readCache;
    }

    private Map<String, Object> getWriteCache() {
        if (writeCache == null) {
            writeCache = new HashMap<>();
        }
        return writeCache;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof AbstractEmbeddedPropertyContainer))
            return false;

        AbstractEmbeddedPropertyContainer<?> that = (AbstractEmbeddedPropertyContainer<?>) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public final String toString() {
        return delegate.toString();
    }
}
