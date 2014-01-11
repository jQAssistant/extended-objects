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

/**
 * Generic implementation of a {@link CdoManager}.
 *
 * @param <EntityId>              The type of entity ids as provided by the datastore.
 * @param <Entity>                The type entities as provided by the datastore.
 * @param <EntityMetadata>        The type of entity metadata as provided by the datastore.
 * @param <EntityDiscriminator>   The type of discriminators as provided by the datastore.
 * @param <RelationId>            The type of relation ids as provided by the datastore.
 * @param <Relation>              The type of relations as provided by the datastore.
 * @param <RelationMetadata>      The type of relation metadata as provided by the datastore.
 * @param <RelationDiscriminator> The type of relation discriminators as provided by the datastore.
 */
public class CdoManagerImpl<EntityId, Entity, EntityMetadata extends DatastoreEntityMetadata<EntityDiscriminator>, EntityDiscriminator, RelationId, Relation, RelationMetadata extends DatastoreRelationMetadata<RelationDiscriminator>, RelationDiscriminator> implements CdoManager {

    private final TransactionalCache<EntityId> entityCache;
    private final TransactionalCache<RelationId> relationCache;
    private final MetadataProvider<EntityMetadata, EntityDiscriminator, RelationMetadata, RelationDiscriminator> metadataProvider;
    private final CdoTransaction cdoTransaction;
    private final PropertyManager<EntityId, Entity, RelationId, Relation> propertyManager;
    private final DatastoreSession<EntityId, Entity, EntityMetadata, EntityDiscriminator, RelationId, Relation, RelationMetadata, RelationDiscriminator> datastoreSession;
    private final InstanceManager<EntityId, Entity, EntityDiscriminator, RelationId, Relation, RelationDiscriminator> instanceManager;
    private final ProxyFactory proxyFactory;
    private final InterceptorFactory interceptorFactory;
    private final InstanceValidator instanceValidator;

    /**
     * Constructor.
     *
     * @param metadataProvider   The {@link com.buschmais.cdo.impl.MetadataProvider}.
     * @param cdoTransaction     The associated {@link com.buschmais.cdo.api.CdoTransaction}.
     * @param entityCache        The associated transactional entity cache.
     * @param relationCache      The associated transactional relation cache.
     * @param datastoreSession   The associated {@link com.buschmais.cdo.spi.datastore.DatastoreSession}.
     * @param instanceManager    The associated {@link com.buschmais.cdo.impl.InstanceManager}.
     * @param proxyFactory
     * @param interceptorFactory The associated {@link com.buschmais.cdo.impl.interceptor.InterceptorFactory}.
     * @param instanceValidator  The associated {@link com.buschmais.cdo.impl.validation.InstanceValidator}.
     */
    public CdoManagerImpl(MetadataProvider<EntityMetadata, EntityDiscriminator, RelationMetadata, RelationDiscriminator> metadataProvider, CdoTransaction cdoTransaction, TransactionalCache<EntityId> entityCache, TransactionalCache<RelationId> relationCache, PropertyManager<EntityId, Entity, RelationId, Relation> propertyManager, DatastoreSession<EntityId, Entity, EntityMetadata, EntityDiscriminator, RelationId, Relation, RelationMetadata, RelationDiscriminator> datastoreSession, InstanceManager<EntityId, Entity, EntityDiscriminator, RelationId, Relation, RelationDiscriminator> instanceManager, ProxyFactory proxyFactory, InterceptorFactory interceptorFactory, InstanceValidator instanceValidator) {
        this.metadataProvider = metadataProvider;
        this.cdoTransaction = cdoTransaction;
        this.entityCache = entityCache;
        this.relationCache = relationCache;
        this.propertyManager = propertyManager;
        this.datastoreSession = datastoreSession;
        this.instanceManager = instanceManager;
        this.proxyFactory = proxyFactory;
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
        return new TransactionalResultIterable<>(new AbstractResultIterable<T>() {
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
                        return instanceManager.getEntityInstance(entity);
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
        Set<EntityDiscriminator> entityDiscriminators = metadataProvider.getEntityDiscriminators(effectiveTypes);
        Entity entity = datastoreSession.create(effectiveTypes, entityDiscriminators);
        return instanceManager.getEntityInstance(entity);
    }

    public <T> T create(Class<T> type) {
        return create(type, new Class<?>[0]).as(type);
    }

    @Override
    public <S, R, T> R create(S source, Class<R> relationType, T target) {
        Entity sourceEntity = instanceManager.getEntity(source);
        Set<Class<?>> sourceTypes = new HashSet<>(Arrays.asList(source.getClass().getInterfaces()));
        RelationTypeMetadata<RelationMetadata> relationTypeMetadata = metadataProvider.getRelationMetadata(relationType);
        Entity targetEntity = instanceManager.getEntity(target);
        Set<Class<?>> targetTypes = new HashSet<>(Arrays.asList(target.getClass().getInterfaces()));
        RelationTypeMetadata.Direction direction = metadataProvider.getRelationDirection(sourceTypes, relationTypeMetadata, targetTypes);
        Relation relation = propertyManager.createSingleRelation(sourceEntity, relationTypeMetadata, direction, targetEntity);
        return instanceManager.getRelationInstance(relation);
    }

    @Override
    public <T, M> CompositeObject migrate(T instance, MigrationStrategy<T, M> migrationStrategy, Class<M> targetType, Class<?>... targetTypes) {
        Entity entity = instanceManager.getEntity(instance);
        Set<EntityDiscriminator> entityDiscriminators = datastoreSession.getEntityDiscriminators(entity);
        TypeMetadataSet<EntityTypeMetadata<EntityMetadata>> types = metadataProvider.getTypes(entityDiscriminators);
        TypeMetadataSet<EntityTypeMetadata<EntityMetadata>> effectiveTargetTypes = getEffectiveTypes(targetType, targetTypes);
        Set<EntityDiscriminator> targetEntityDiscriminators = metadataProvider.getEntityDiscriminators(effectiveTargetTypes);
        datastoreSession.migrate(entity, types, entityDiscriminators, effectiveTargetTypes, targetEntityDiscriminators);
        instanceManager.removeEntityInstance(instance);
        CompositeObject migratedInstance = instanceManager.getEntityInstance(entity);
        if (migrationStrategy != null) {
            migrationStrategy.migrate(instance, migratedInstance.as(targetType));
        }
        instanceManager.destroyInstance(instance);
        return migratedInstance;
    }

    @Override
    public <T, M> CompositeObject migrate(T instance, Class<M> targetType, Class<?>... targetTypes) {
        return migrate(instance, null, targetType, targetTypes);
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
        if (instanceManager.isEntity(instance)) {
            Entity entity = instanceManager.getEntity(instance);
            datastoreSession.deleteEntity(entity);
            instanceManager.removeEntityInstance(instance);
        } else if (instanceManager.isRelation(instance)) {
            Relation relation = instanceManager.getRelation(instance);
            datastoreSession.getDatastorePropertyManager().deleteRelation(relation);
            instanceManager.removeRelationInstance(instance);
        } else {
            throw new CdoException(instance + " is not a managed CDO instance.");
        }
        instanceManager.destroyInstance(instance);
    }

    @Override
    public Query<CompositeRowObject> createQuery(String query) {
        CdoQueryImpl<CompositeRowObject, String> cdoQuery = new CdoQueryImpl<>(query, datastoreSession, instanceManager, proxyFactory, cdoTransaction, interceptorFactory, Collections.<Class<?>>emptyList());
        return interceptorFactory.addInterceptor(cdoQuery);
    }

    @Override
    public <T> Query<T> createQuery(String query, Class<T> type) {
        CdoQueryImpl<T, String> cdoQuery = new CdoQueryImpl<>(query, datastoreSession, instanceManager, proxyFactory, cdoTransaction, interceptorFactory, Arrays.asList(new Class<?>[]{type}));
        return interceptorFactory.addInterceptor(cdoQuery);
    }

    @Override
    public Query<CompositeRowObject> createQuery(String query, Class<?> type, Class<?>... types) {
        CdoQueryImpl<CompositeRowObject, String> cdoQuery = new CdoQueryImpl<>(query, datastoreSession, instanceManager, proxyFactory, cdoTransaction, interceptorFactory, Arrays.asList(types));
        return interceptorFactory.addInterceptor(cdoQuery);
    }

    @Override
    public <T> Query<T> createQuery(Class<T> query) {
        CdoQueryImpl<T, Class<T>> cdoQuery = new CdoQueryImpl<>(query, datastoreSession, instanceManager, proxyFactory, cdoTransaction, interceptorFactory, Arrays.asList(new Class<?>[]{query}));
        return interceptorFactory.addInterceptor(cdoQuery);
    }

    @Override
    public <Q> Query<CompositeRowObject> createQuery(Class<Q> query, Class<?>... types) {
        CdoQueryImpl<CompositeRowObject, Class<Q>> cdoQuery = new CdoQueryImpl<>(query, datastoreSession, instanceManager, proxyFactory, cdoTransaction, interceptorFactory, Arrays.asList(types));
        return interceptorFactory.addInterceptor(cdoQuery);
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
        for (Object instance : relationCache.values()) {
            Relation relation = instanceManager.getRelation(instance);
            datastoreSession.flushRelation(relation);
        }
        for (Object instance : entityCache.values()) {
            Entity entity = instanceManager.getEntity(instance);
            datastoreSession.flushEntity(entity);
        }
    }

    private TypeMetadataSet<EntityTypeMetadata<EntityMetadata>> getEffectiveTypes(Class<?> type, Class<?>... types) {
        TypeMetadataSet<EntityTypeMetadata<EntityMetadata>> effectiveTypes = new TypeMetadataSet<>();
        effectiveTypes.add(metadataProvider.getEntityMetadata(type));
        for (Class<?> otherType : types) {
            effectiveTypes.add(metadataProvider.getEntityMetadata(otherType));
        }
        return effectiveTypes;
    }
}
