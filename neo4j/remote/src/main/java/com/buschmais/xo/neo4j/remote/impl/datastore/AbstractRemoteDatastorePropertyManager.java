package com.buschmais.xo.neo4j.remote.impl.datastore;

import java.util.HashMap;
import java.util.Map;

import com.buschmais.xo.api.metadata.method.PrimitivePropertyMethodMetadata;
import com.buschmais.xo.neo4j.remote.impl.model.AbstractRemotePropertyContainer;
import com.buschmais.xo.neo4j.remote.impl.model.state.AbstractPropertyContainerState;
import com.buschmais.xo.neo4j.spi.metadata.PropertyMetadata;
import com.buschmais.xo.spi.datastore.DatastorePropertyManager;

import org.neo4j.driver.types.Entity;

import static org.neo4j.driver.Values.parameters;

public abstract class AbstractRemoteDatastorePropertyManager<T extends AbstractRemotePropertyContainer>
    implements DatastorePropertyManager<T, PropertyMetadata> {

    protected StatementExecutor statementExecutor;

    protected RemoteDatastoreSessionCache datastoreSessionCache;

    public AbstractRemoteDatastorePropertyManager(StatementExecutor statementExecutor, RemoteDatastoreSessionCache datastoreSessionCache) {
        this.statementExecutor = statementExecutor;
        this.datastoreSessionCache = datastoreSessionCache;
    }

    @Override
    public void setProperty(T entity, PrimitivePropertyMethodMetadata<PropertyMetadata> metadata, Object value) {
        ensureLoaded(entity);
        entity.setProperty(metadata.getDatastoreMetadata()
            .getName(), value);
    }

    @Override
    public boolean hasProperty(T entity, PrimitivePropertyMethodMetadata<PropertyMetadata> metadata) {
        ensureLoaded(entity);
        return entity.hasProperty(metadata.getDatastoreMetadata()
            .getName());
    }

    @Override
    public void removeProperty(T entity, PrimitivePropertyMethodMetadata<PropertyMetadata> metadata) {
        ensureLoaded(entity);
        entity.removeProperty(metadata.getDatastoreMetadata()
            .getName());
    }

    @Override
    public Object getProperty(T entity, PrimitivePropertyMethodMetadata<PropertyMetadata> metadata) {
        ensureLoaded(entity);
        return entity.getProperty(metadata.getDatastoreMetadata()
            .getName());
    }

    protected void flush(StatementBatchBuilder batchBuilder, T entity, String pattern, String identifier) {
        AbstractPropertyContainerState state = entity.getState();
        Map<String, Object> writeCache = state.getWriteCache();
        if (writeCache != null && !writeCache.isEmpty()) {
            String statement =
                "MATCH " + pattern + " WHERE id(" + identifier + ")=entry['id'] SET " + identifier + "+=entry['" + identifier + "'] RETURN collect(id("
                    + identifier + "))";
            batchBuilder.add(statement, parameters("id", entity.getId(), identifier, writeCache));
        }
    }

    @Override
    public final void afterCompletion(T entity, boolean clear) {
        entity.getState()
            .afterCompletion(clear);
    }

    protected final void ensureLoaded(T entity) {
        if (!entity.getState()
            .isLoaded()) {
            Entity state = load(entity);
            entity.getState()
                .load(state.asMap());
        }
    }

    protected Map<String, Object> getProperties(Map<PrimitivePropertyMethodMetadata<PropertyMetadata>, Object> exampleEntity) {
        Map<String, Object> properties = new HashMap<>(exampleEntity.size());
        for (Map.Entry<PrimitivePropertyMethodMetadata<PropertyMetadata>, Object> entry : exampleEntity.entrySet()) {
            properties.put(entry.getKey()
                .getDatastoreMetadata()
                .getName(), entry.getValue());
        }
        return properties;
    }

    protected abstract Entity load(T entity);

}
