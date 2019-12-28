package com.buschmais.xo.neo4j.remote.impl.model;

import java.util.Collections;
import java.util.Map;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.neo4j.api.model.Neo4jPropertyContainer;
import com.buschmais.xo.neo4j.remote.impl.model.state.AbstractPropertyContainerState;

/**
 * Abstract base class for property containers.
 *
 * NOTE: This class and all deriving classes must not override
 * {@link #equals(Object)} and {@link #hashCode()}, equality is defined by
 * reference.
 *
 * @param <S>
 *            The state type.
 */
public abstract class AbstractRemotePropertyContainer<S extends AbstractPropertyContainerState> implements Neo4jPropertyContainer {

    private final S state;

    private final int hashCode;

    private long id;

    protected AbstractRemotePropertyContainer(long id, S state) {
        this.id = id;
        this.state = state;
        this.hashCode = super.hashCode();
    }

    @Override
    public long getId() {
        return id;
    }

    public void updateId(Long id) {
        if (this.id < 0) {
            this.id = id;
        } else {
            throw new XOException("Cannot update persistent id of " + this);
        }
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

    public void setProperty(String key, Object value) {
        state.getOrCreateWriteCache().put(key, value);
        state.getReadCache().put(key, value);
    }

    public void removeProperty(String name) {
        setProperty(name, null);
    }

    @Override
    public final boolean equals(Object other) {
        return this == other;
    }

    @Override
    public final int hashCode() {
        return this.hashCode;
    }

    @Override
    public final String toString() {
        return getClass().getSimpleName() + "{" + "id=" + id + '}';
    }

}
