package com.buschmais.cdo.impl;

import com.buschmais.cdo.api.*;
import com.buschmais.cdo.impl.cache.TransactionalCache;
import com.buschmais.cdo.impl.interceptor.InterceptorFactory;
import com.buschmais.cdo.impl.query.CdoQueryImpl;
import com.buschmais.cdo.impl.validation.InstanceValidator;
import com.buschmais.cdo.spi.datastore.DatastoreSession;
import com.buschmais.cdo.spi.datastore.TypeSet;
import com.buschmais.cdo.spi.metadata.MetadataProvider;

import javax.validation.ConstraintViolation;
import java.util.Arrays;
import java.util.Set;

public class CdoManagerImpl<EntityId, Entity, RelationId, Relation> implements CdoManager {

    private final MetadataProvider metadataProvider;
    private final TransactionalCache<?> cache;
    private final CdoTransaction cdoTransaction;
    private final DatastoreSession<EntityId, Entity, RelationId, Relation> datastoreSession;
    private final InstanceManager<EntityId, Entity> instanceManager;
    private final InterceptorFactory interceptorFactory;
    private final InstanceValidator instanceValidator;

    public CdoManagerImpl(MetadataProvider metadataProvider, TransactionalCache<EntityId> cache, CdoTransaction cdoTransaction, DatastoreSession<EntityId, Entity, RelationId, Relation> datastoreSession, InstanceManager instanceManager, InterceptorFactory interceptorFactory, InstanceValidator instanceValidator) {
        this.metadataProvider = metadataProvider;
        this.cache = cache;
        this.cdoTransaction = cdoTransaction;
        this.datastoreSession = datastoreSession;
        this.instanceManager = instanceManager;
        this.interceptorFactory = interceptorFactory;
        this.instanceValidator = instanceValidator;
    }

    @Override
    public CdoTransaction currentTransaction() {
        return cdoTransaction;
    }

    @Override
    public Set<ConstraintViolation<Object>> validate() {
        return instanceValidator.validate();
    }

    @Override
    public <T> ResultIterable<T> find(final Class<T> type, final Object value) {
        final ResultIterator<Entity> iterator = datastoreSession.find(type, value);
        return new AbstractResultIterable<T>() {
            @Override
            public ResultIterator<T> iterator() {
                return new ResultIterator<T>() {

                    @Override
                    public boolean hasNext() {
                        return iterator.hasNext();
                    }

                    @Override
                    public T next() {
                        Entity entity = iterator.next();
                        return instanceManager.getInstance(entity);
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException("Cannot remove instance.");
                    }

                    @Override
                    public void close() {
                        iterator.close();
                    }
                };
            }
        };
    }

    @Override
    public CompositeObject create(Class type, Class<?>... types) {
        TypeSet effectiveTypes = getEffectiveTypes(type, types);
        Entity entity = datastoreSession.create(effectiveTypes);
        CompositeObject instance = instanceManager.getInstance(entity);
        return instance;
    }

    public <T> T create(Class<T> type) {
        T instance = create(type, new Class<?>[0]).as(type);
        return instance;
    }

    @Override
    public <T, M> CompositeObject migrate(T instance, MigrationStrategy<T, M> migrationStrategy, Class<M> targetType, Class<?>... targetTypes) {
        Entity entity = instanceManager.getEntity(instance);
        TypeSet types = metadataProvider.getDatastoreMetadataProvider().getTypes(entity);
        TypeSet effectiveTargetTypes = getEffectiveTypes(targetType, targetTypes);
        datastoreSession.migrate(entity, types, effectiveTargetTypes);
        instanceManager.removeInstance(instance);
        CompositeObject migratedInstance = instanceManager.getInstance(entity);
        if (migrationStrategy != null) {
            migrationStrategy.migrate(instance, migratedInstance.as(targetType));
        }
        instanceManager.destroyInstance(instance);
        return migratedInstance;
    }

    @Override
    public <T, M> CompositeObject migrate(T instance, Class<M> targetType, Class<?>... targetTypes) {
        return migrate(instance, null, targetTypes);
    }

    @Override
    public <T, M> M migrate(T instance, MigrationStrategy<T, M> migrationStrategy, Class<M> targetType) {
        return migrate(instance, migrationStrategy, targetType, new Class<?>[0]).as(targetType);
    }

    @Override
    public <T, M> M migrate(T instance, Class<M> targetType) {
        return migrate(instance, null, targetType);
    }

    @Override
    public <T> void delete(T instance) {
        Entity entity = instanceManager.getEntity(instance);
        instanceManager.removeInstance(instance);
        instanceManager.destroyInstance(instance);
        datastoreSession.delete(entity);
    }

    @Override
    public <QL> Query createQuery(QL query, Class<?>... types) {
        return new CdoQueryImpl(query, datastoreSession, instanceManager, interceptorFactory, Arrays.asList(types));
    }

    @Override
    public void close() {
        instanceManager.close();
    }

    @Override
    public <DS> DS getDatastoreSession(Class<DS> sessionType) {
        return sessionType.cast(datastoreSession);
    }

    @Override
    public void flush() {
        datastoreSession.flush((Iterable<Entity>) cache.values());
    }

    private TypeSet getEffectiveTypes(Class<?> type, Class<?>... types) {
        TypeSet effectiveTypes = new TypeSet();
        effectiveTypes.add(type);
        effectiveTypes.addAll(Arrays.asList(types));
        return effectiveTypes;
    }
}
