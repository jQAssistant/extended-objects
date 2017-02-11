package com.buschmais.xo.neo4j.remote.api;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.buschmais.xo.neo4j.api.model.Neo4jPropertyContainer;
import com.buschmais.xo.neo4j.remote.impl.model.RemoteRelationship;

public abstract class AbstractRemotePropertyContainer implements Neo4jPropertyContainer {

    private long id;

    private Map<String, Object> properties = new HashMap<>();

    private Map<String, Object> writeCache = null;

    protected AbstractRemotePropertyContainer(long id) {
        this.id = id;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public boolean hasProperty(String key) {
        return properties.containsKey(key);
    }

    @Override
    public Object getProperty(String key) {
        return properties.get(key);
    }

    @Override
    public Map<String, Object> getProperties() {
        return Collections.unmodifiableMap(properties);
    }

    public void setProperty(String key, Object value) {
        if (writeCache == null) {
            writeCache = new HashMap();
        }
        writeCache.put(key, value);
        properties.put(key, value);
    }

    public Map<String, Object> getWriteCache() {
        return writeCache;
    }

    public void removeProperty(String name) {
        setProperty(name, null);
    }


    @Override
    public final boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        AbstractRemotePropertyContainer that = (AbstractRemotePropertyContainer) o;

        return id == that.id;
    }

    @Override
    public final int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

}
