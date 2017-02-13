package com.buschmais.xo.neo4j.remote.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.buschmais.xo.neo4j.remote.impl.model.state.AbstractPropertyContainerState;
import org.neo4j.driver.v1.Record;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.neo4j.remote.impl.model.AbstractRemotePropertyContainer;
import com.buschmais.xo.neo4j.spi.metadata.PropertyMetadata;
import com.buschmais.xo.spi.datastore.DatastorePropertyManager;
import com.buschmais.xo.spi.metadata.method.PrimitivePropertyMethodMetadata;

public abstract class AbstractRemoteDatastorePropertyManager<T extends AbstractRemotePropertyContainer, S extends AbstractPropertyContainerState>
        implements DatastorePropertyManager<T, PropertyMetadata> {

    protected RemoteDatastoreTransaction transaction;

    public AbstractRemoteDatastorePropertyManager(RemoteDatastoreTransaction transaction) {
        this.transaction = transaction;
    }

    @Override
    public void setProperty(T entity, PrimitivePropertyMethodMetadata<PropertyMetadata> metadata, Object value) {
        ensureLoaded(entity);
        entity.setProperty(metadata.getDatastoreMetadata().getName(), value);
    }


    @Override
    public boolean hasProperty(T entity, PrimitivePropertyMethodMetadata<PropertyMetadata> metadata) {
        ensureLoaded(entity);
        return entity.hasProperty(metadata.getDatastoreMetadata().getName());
    }

    @Override
    public void removeProperty(T entity, PrimitivePropertyMethodMetadata<PropertyMetadata> metadata) {
        ensureLoaded(entity);
        entity.removeProperty(metadata.getDatastoreMetadata().getName());
    }

    @Override
    public Object getProperty(T entity, PrimitivePropertyMethodMetadata<PropertyMetadata> metadata) {
        ensureLoaded(entity);
        return entity.getProperty(metadata.getDatastoreMetadata().getName());
    }

    @Override
    public final void flush(Iterable<T> entities) {
        Map<Long, Map<String, Object>> entityProperties = new HashMap<>();
        for (T entity : entities) {
            Map<String, Object> writeCache = entity.getWriteCache();
            if (writeCache != null && !writeCache.isEmpty()) {
                entityProperties.put(entity.getId(), writeCache);
            }
        }
        if (!entityProperties.isEmpty()) {
            StringBuilder match = new StringBuilder();
            StringBuilder where = new StringBuilder();
            StringBuilder set = new StringBuilder();
            int i = 0;
            Map<String, Object> parameters = new HashMap<>();
            for (Map.Entry<Long, Map<String, Object>> entry : entityProperties.entrySet()) {
                Long id = entry.getKey();
                String identifier = createIdentifier(i);
                parameters.put(identifier, id);
                if (match.length() > 0) {
                    match.append(',');
                    where.append(" and ");
                }
                match.append(createIdentifierPattern(identifier));
                where.append(String.format("id(%s)={%s}", identifier, identifier));
                Map<String, Object> properties = entry.getValue();
                for (Map.Entry<String, Object> propertyEntry : properties.entrySet()) {
                    String property = propertyEntry.getKey();
                    Object value = propertyEntry.getValue();
                    String parameterName = identifier + '_' + property;
                    if (set.length() > 0) {
                        set.append(',');
                    }
                    set.append(String.format("%s.%s={%s}", identifier, property, parameterName));
                    parameters.put(parameterName, value);
                }
                i++;
            }
            StringBuilder statement = new StringBuilder().append("MATCH ").append(match).append(" WHERE ").append(where).append(" SET ").append(set)
                    .append(" RETURN count(*) as nodes");
            Record record = transaction.getStatementRunner().run(statement.toString(), parameters).single();
            long nodes = record.get("nodes").asLong();
            if (nodes != 1) {
                throw new XOException("Cannot flush properties.");
            }
        }
    }

    @Override
    public final void clear(Iterable<T> entities) {
        for (T entity : entities) {
            entity.clear();
        }
    }

    protected final void ensureLoaded(T entity) {
        if (entity.getState() == null) {
            S state = load(entity);
            entity.load(state);
        }
    }

    protected abstract String createIdentifier(int i);

    protected abstract String createIdentifierPattern(String identifier);

    protected abstract S load(T entity);

}
