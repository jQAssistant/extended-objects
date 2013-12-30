package com.buschmais.cdo.impl;

import com.buschmais.cdo.api.*;
import com.buschmais.cdo.impl.cache.TransactionalCache;
import com.buschmais.cdo.impl.interceptor.InterceptorFactory;
import com.buschmais.cdo.impl.transaction.TransactionalResultIterable;
import com.buschmais.cdo.impl.validation.InstanceValidator;
import com.buschmais.cdo.impl.query.CdoQueryImpl;
import com.buschmais.cdo.spi.datastore.DatastoreEntityMetadata;
import com.buschmais.cdo.spi.datastore.DatastoreRelationMetadata;
import com.buschmais.cdo.spi.datastore.DatastoreSession;
import com.buschmais.cdo.spi.datastore.TypeMetadataSet;
import com.buschmais.cdo.spi.metadata.type.EntityTypeMetadata;
import com.buschmais.cdo.spi.metadata.type.RelationTypeMetadata;

import javax.validation.ConstraintViolation;
import java.util.*;

import static com.buschmais.cdo.api.Query.Result.CompositeRowObject;

public class CdoManagerImpl<EntityId, Entity, EntityMetadata extends DatastoreEntityMetadata<EntityDiscriminator>, EntityDiscriminator, RelationId, Relation, RelationMetadata extends DatastoreRelationMetadata<RelationDiscriminator>, RelationDiscriminator> implements CdoManager {

    private final TransactionalCache cache;
    private final MetadataProvider metadataProvider;
    private final CdoTransaction cdoTransaction;
    private final DatastoreSession<EntityId, Entity, EntityMetadata, EntityDiscriminator, RelationId, Relation> datastoreSession;
    private final InstanceManager<EntityId, Entity> instanceManager;
    private final InterceptorFactory interceptorFactory;
    private final InstanceValidator instanceValidator;

    public CdoManagerImpl(MetadataProvider metadataProvider, CdoTransaction cdoTransaction, TransactionalCache cache, DatastoreSession<EntityId, Entity, EntityMetadata, EntityDiscriminator, RelationId, Relation> datastoreSession, InstanceManager instanceManager, InterceptorFactory interceptorFactory, InstanceValidator instanceValidator) {
        this.metadataProvider = metadataProvider;
        this.cdoTransaction = cdoTransaction;
        this.cache = cache;
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
        EntityTypeMetadata<EntityMetadata> entityTypeMetadata = metadataProvider.getEntityMetadata(type);
        EntityDiscriminator entityDiscriminator = entityTypeMetadata.getDatastoreMetadata().getDiscriminator();
        if (entityDiscriminator == null) {
            throw new CdoException("Type " + type.getName() + " has no discriminator (i.e. cannot be identified in datastore).");
        }
        final ResultIterator<Entity> iterator = datastoreSession.find(entityTypeMetadata, entityDiscriminator, value);
        return new TransactionalResultIterable<T>(new AbstractResultIterable<T>() {
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
        }, cdoTransaction);
    }

    @Override
    public CompositeObject create(Class type, Class<?>... types) {
        TypeMetadataSet<EntityTypeMetadata<EntityMetadata>> effectiveTypes = getEffectiveTypes(type, types);
        Set<EntityDiscriminator> entityDiscriminators = metadataProvider.getDiscriminators(effectiveTypes);
        Entity entity = datastoreSession.create(effectiveTypes, entityDiscriminators);
        CompositeObject instance = instanceManager.getInstance(entity);
        return instance;
    }

    public <T> T create(Class<T> type) {
        T instance = create(type, new Class<?>[0]).as(type);
        return instance;
    }

    @Override
    public <S, R, T> R create(S source, Class<R> relationType, T target) {
        Entity sourceEntity = instanceManager.getEntity(source);
        List<Class<?>> sourceTypes = Arrays.asList(source.getClass().getInterfaces());
        RelationTypeMetadata<RelationTypeMetadata> relationMetadata = metadataProvider.getRelationMetadata(relationType);
        Entity targetEntity = instanceManager.getEntity(target);
        List<Class<?>> targetTypes = Arrays.asList(target.getClass().getInterfaces());
        RelationTypeMetadata.Direction direction = metadataProvider.getRelationDirection(sourceTypes, relationMetadata, targetTypes);
        datastoreSession.getDatastorePropertyManager().createRelation(sourceEntity, relationMetadata.getDatastoreMetadata(), direction, targetEntity);
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T, M> CompositeObject migrate(T instance, MigrationStrategy<T, M> migrationStrategy, Class<M> targetType, Class<?>... targetTypes) {
        Entity entity = instanceManager.getEntity(instance);
        Set<EntityDiscriminator> entityDiscriminators = datastoreSession.getDiscriminators(entity);
        TypeMetadataSet types = metadataProvider.getTypes(entityDiscriminators);
        TypeMetadataSet effectiveTargetTypes = getEffectiveTypes(targetType, targetTypes);
        Set<EntityDiscriminator> targetEntityDiscriminators = metadataProvider.getDiscriminators(effectiveTargetTypes);
        datastoreSession.migrate(entity, types, entityDiscriminators, effectiveTargetTypes, targetEntityDiscriminators);
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
    public Query<CompositeRowObject> createQuery(String query) {
        return interceptorFactory.addInterceptor(new CdoQueryImpl(query, datastoreSession, instanceManager, cdoTransaction, interceptorFactory, Collections.emptyList()));
    }

    @Override
    public <T> Query<T> createQuery(String query, Class<T> type) {
        return interceptorFactory.addInterceptor(new CdoQueryImpl(query, datastoreSession, instanceManager, cdoTransaction, interceptorFactory, Arrays.asList(new Class<?>[]{type})));
    }

    @Override
    public Query<CompositeRowObject> createQuery(String query, Class<?> type, Class<?>... types) {
        return interceptorFactory.addInterceptor(new CdoQueryImpl(query, datastoreSession, instanceManager, cdoTransaction, interceptorFactory, Arrays.asList(types)));
    }

    @Override
    public <T> Query<T> createQuery(Class<T> query) {
        return interceptorFactory.addInterceptor(new CdoQueryImpl(query, datastoreSession, instanceManager, cdoTransaction, interceptorFactory, Arrays.asList(new Class<?>[]{query})));
    }

    @Override
    public Query<CompositeRowObject> createQuery(Class<?> query, Class<?>... types) {
        return interceptorFactory.addInterceptor(new CdoQueryImpl(query, datastoreSession, instanceManager, cdoTransaction, interceptorFactory, Arrays.asList(types)));
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
        Collection instances = cache.values();
        for (Object instance : instances) {
            Entity entity = instanceManager.getEntity(instance);
            datastoreSession.flush(entity);
        }
    }

    private TypeMetadataSet<EntityTypeMetadata<EntityMetadata>> getEffectiveTypes(Class<?> type, Class<?>... types) {
        TypeMetadataSet<EntityTypeMetadata<EntityMetadata>> effectiveTypes = new TypeMetadataSet();
        effectiveTypes.add(metadataProvider.getEntityMetadata(type));
        for (Class<?> otherType : types) {
            effectiveTypes.add(metadataProvider.getEntityMetadata(otherType));
        }
        return effectiveTypes;
    }
}
