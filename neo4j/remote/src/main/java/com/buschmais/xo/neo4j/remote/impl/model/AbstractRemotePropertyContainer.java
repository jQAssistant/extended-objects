package com.buschmais.xo.neo4j.remote.impl.model;

import java.util.Collections;
import java.util.Map;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.neo4j.api.model.Neo4jPropertyContainer;
import com.buschmais.xo.neo4j.remote.impl.model.state.AbstractPropertyContainerState;

import lombok.ToString;

/**
 * Abstract base class for property containers.
 * <p>
 * NOTE: This class and all deriving classes must not override
 * {@link #equals(Object)} and {@link #hashCode()}, equality is defined by
 * reference.
 *
 * @param <S>
 *     The state type.
 */
@ToString
public abstract class AbstractRemotePropertyContainer<S extends AbstractPropertyContainerState> implements Neo4jPropertyContainer {

    private final S state;

    private final long initialId;

    private long id;

    protected AbstractRemotePropertyContainer(long id, S state) {
        this.initialId = id;
        this.id = id;
        this.state = state;
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
        return state.getReadCache()
            .containsKey(key);
    }

    @Override
    public Object getProperty(String key) {
        return state.getReadCache()
            .get(key);
    }

    @Override
    public Map<String, Object> getProperties() {
        return Collections.unmodifiableMap(state.getReadCache());
    }

    public void setProperty(String key, Object value) {
        Object existingValue = getProperty(key);
        if ((existingValue == null && value != null) || (existingValue != null && !existingValue.equals(value))) {
            state.getOrCreateWriteCache()
                .put(key, value);
            state.getReadCache()
                .put(key, value);
        }
    }

    public void removeProperty(String name) {
        setProperty(name, null);
    }

    @Override
    public final boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (other != null && other.getClass()
            .equals(this.getClass())) {
            AbstractRemotePropertyContainer<?> otherContainer = (AbstractRemotePropertyContainer<?>) other;
            return this.id == otherContainer.id || this.id == otherContainer.initialId || this.initialId == otherContainer.id;
        }
        return false;
    }

    @Override
    public final int hashCode() {
        return (int) this.initialId;
    }

}
