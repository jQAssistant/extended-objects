package com.buschmais.xo.neo4j.remote.impl.model;

import java.util.Collections;
import java.util.Map;

import com.buschmais.xo.neo4j.api.model.Neo4jPropertyContainer;
import com.buschmais.xo.neo4j.remote.impl.model.state.AbstractPropertyContainerState;

public abstract class AbstractRemotePropertyContainer<S extends AbstractPropertyContainerState> implements Neo4jPropertyContainer {

    private long id;

    private S state;

    protected AbstractRemotePropertyContainer(long id, S state) {
        this.id = id;
        this.state = state;
    }

    @Override
    public long getId() {
        return id;
    }

    public S getState() {
        return state;
    }

    @Override
    public boolean hasProperty(String key) {
        return state.getReadCache().containsKey(key);
    }

    @Override
    public Object getProperty(String key) {
        return state.getReadCache().get(key);
    }

    @Override
    public Map<String, Object> getProperties() {
        return Collections.unmodifiableMap(state.getReadCache());
    }

    public void load(S state) {
        this.state = state;
    }

    public void setProperty(String key, Object value) {
        state.getOrCreateWriteCache().put(key, value);
        state.getReadCache().put(key, value);
    }

    public void clear() {
        state = null;
    }

    public Map<String, Object> getWriteCache() {
        return state.getWriteCache();
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
