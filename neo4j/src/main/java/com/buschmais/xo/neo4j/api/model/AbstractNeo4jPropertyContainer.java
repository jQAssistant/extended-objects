package com.buschmais.xo.neo4j.api.model;

import java.util.Map;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.PropertyContainer;

import com.buschmais.xo.api.XOException;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

public abstract class AbstractNeo4jPropertyContainer<T extends PropertyContainer> implements PropertyContainer {

    protected T delegate;

    private Object2ObjectOpenHashMap<String, Object> readCache = null;
    private Object2ObjectOpenHashMap<String, Object> writeCache = null;

    public AbstractNeo4jPropertyContainer(T delegate) {
        this.delegate = delegate;
    }

    @Override
    public GraphDatabaseService getGraphDatabase() {
        return delegate.getGraphDatabase();
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
        Object2ObjectOpenHashMap<String, Object> readCache = getReadCache();
        if (readCache.containsKey(key)) {
            return readCache.get(key);
        }
        Object value = delegate.getProperty(key);
        readCache.put(key, value);
        return value;
    };

    @Override
    public Object getProperty(String key, Object defaultValue) {
        throw new XOException("Unsupported operation");
    }

    @Override
    public void setProperty(String key, Object value) {
        getWriteCache().put(key, value);
        getReadCache().put(key, value);
    }

    @Override
    public Object removeProperty(String key) {
        getWriteCache().remove(key);
        getReadCache().remove(key);
        return delegate.removeProperty(key);
    }

    @Override
    public Iterable<String> getPropertyKeys() {
        return delegate.getPropertyKeys();
    }

    @Override
    public Map<String, Object> getProperties(String... keys) {
        throw new XOException("Unsupported operation");
    }

    @Override
    public Map<String, Object> getAllProperties() {
        throw new XOException("Unsupported operation");
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

    private Object2ObjectOpenHashMap<String, Object> getReadCache() {
        if (readCache == null) {
            readCache = new Object2ObjectOpenHashMap<>();
        }
        return readCache;
    }

    private Object2ObjectOpenHashMap<String, Object> getWriteCache() {
        if (writeCache == null) {
            writeCache = new Object2ObjectOpenHashMap<>();
        }
        return writeCache;
    }

    @Override
    public final int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public final boolean equals(Object obj) {
        if (obj instanceof AbstractNeo4jPropertyContainer<?>) {
            return delegate.equals(((AbstractNeo4jPropertyContainer<?>) obj).delegate);
        }
        return false;
    }

    @Override
    public final String toString() {
        return delegate.toString();
    }
}
