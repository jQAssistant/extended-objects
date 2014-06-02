package com.buschmais.xo.impl;

import com.buschmais.xo.api.*;
import com.buschmais.xo.impl.proxy.InstanceInvocationHandler;
import com.buschmais.xo.impl.proxy.example.ExampleProxyMethodService;
import com.buschmais.xo.impl.query.XOQueryImpl;
import com.buschmais.xo.impl.transaction.TransactionalResultIterable;
import com.buschmais.xo.spi.datastore.DatastoreEntityMetadata;
import com.buschmais.xo.spi.datastore.DatastoreRelationMetadata;
import com.buschmais.xo.spi.datastore.DatastoreSession;
import com.buschmais.xo.spi.datastore.TypeMetadataSet;
import com.buschmais.xo.spi.metadata.method.AbstractRelationPropertyMethodMetadata;
import com.buschmais.xo.spi.metadata.method.IndexedPropertyMethodMetadata;
import com.buschmais.xo.spi.metadata.method.PrimitivePropertyMethodMetadata;
import com.buschmais.xo.spi.metadata.type.EntityTypeMetadata;

import javax.validation.ConstraintViolation;
import java.util.*;

import static com.buschmais.xo.api.Query.Result.CompositeRowObject;
import static com.buschmais.xo.spi.metadata.type.RelationTypeMetadata.Direction.FROM;
import static com.buschmais.xo.spi.metadata.type.RelationTypeMetadata.Direction.TO;
import static java.util.Collections.emptyMap;

/**
 * Generic implementation of a {@link com.buschmais.xo.api.XOManager}.
 *
 * @param <EntityId>              The type of entity ids as provided by the datastore.
 * @param <Entity>                The type entities as provided by the datastore.
 * @param <EntityMetadata>        The type of entity metadata as provided by the datastore.
 * @param <EntityDiscriminator>   The type of discriminators as provided by the datastore.
 * @param <RelationId>            The type of relation ids as provided by the datastore.
 * @param <Relation>              The type of relations as provided by the datastore.
 * @param <RelationMetadata>      The type of relation metadata as provided by the datastore.
 * @param <RelationDiscriminator> The type of relation discriminators as provided by the datastore.
 * @param <PropertyMetadata>      The type of property metadata as provided by the datastore.
 */
public class XOManagerImpl<EntityId, Entity, EntityMetadata extends DatastoreEntityMetadata<EntityDiscriminator>, EntityDiscriminator, RelationId, Relation, RelationMetadata extends DatastoreRelationMetadata<RelationDiscriminator>, RelationDiscriminator, PropertyMetadata>
        implements XOManager {

    private final SessionContext<EntityId, Entity, EntityMetadata, EntityDiscriminator, RelationId, Relation, RelationMetadata, RelationDiscriminator, PropertyMetadata> sessionContext;

    /**
     * Constructor.
     *
     * @param sessionContext The associated {@link SessionContext}.
     */
    public XOManagerImpl(
            SessionContext<EntityId, Entity, EntityMetadata, EntityDiscriminator, RelationId, Relation, RelationMetadata, RelationDiscriminator, PropertyMetadata> sessionContext) {
        this.sessionContext = sessionContext;
    }

    @Override
    public XOTransaction currentTransaction() {
        XOTransaction xoTransaction = sessionContext.getXOTransaction();
        if (xoTransaction == null) {
            throw new XOException("No XOTransaction available (e.g. not supported by the datastore).");
        }
        return xoTransaction;
    }

    @Override
    public Set<ConstraintViolation<Object>> validate() {
        return sessionContext.getInstanceValidationService().validate();
    }

    @Override
    public <T> ResultIterable<T> find(final Class<T> type, final Object value) {
        EntityTypeMetadata<EntityMetadata> entityTypeMetadata = sessionContext.getMetadataProvider().getEntityMetadata(type);
        IndexedPropertyMethodMetadata indexedProperty = entityTypeMetadata.getIndexedProperty();
        Map<PrimitivePropertyMethodMetadata<PropertyMetadata>, Object> exampleEntity = new HashMap<>(1);
        if (indexedProperty != null) {
            exampleEntity.put(indexedProperty.getPropertyMethodMetadata(), value);
        } else {
            exampleEntity.put(null, value);
        }
        return findByExample(type, exampleEntity);
    }

    @Override
    public <T> ResultIterable<T> find(Example<T> example, Class<T> type) {
        Map<PrimitivePropertyMethodMetadata<PropertyMetadata>, Object> exampleEntity = prepareExample(example, type);
        return findByExample(type, exampleEntity);
    }

    @Override
    public ResultIterable<CompositeObject> find(Example<CompositeObject> example, Class<?> type, Class<?>... types) {
        Map<PrimitivePropertyMethodMetadata<PropertyMetadata>, Object> exampleEntity = prepareExample(example, type, types);
        return findByExample(type, exampleEntity);
    }

    /**
     * Setup an example entity.
     *
     * @param type    The type.
     * @param example The provided example.
     * @param <T>     The type.
     * @return The example.
     */
    private <T> Map<PrimitivePropertyMethodMetadata<PropertyMetadata>, Object> prepareExample(Example<T> example, Class<?> type, Class<?>... types) {
        Map<PrimitivePropertyMethodMetadata<PropertyMetadata>, Object> exampleEntity = new HashMap<>();
        InstanceInvocationHandler invocationHandler = new InstanceInvocationHandler(exampleEntity, new ExampleProxyMethodService(type, sessionContext));
        List<Class<?>> effectiveTypes = new ArrayList<>();
        effectiveTypes.add(type);
        effectiveTypes.addAll(Arrays.asList(types));
        T instance = sessionContext.getProxyFactory().createInstance(invocationHandler, effectiveTypes.toArray(new Class<?>[effectiveTypes.size()]),
                CompositeObject.class);
        example.prepare(instance);
        return exampleEntity;
    }

    /**
     * Find entities according to the given example entity.
     *
     * @param type   The entity type.
     * @param entity The example entity.
     * @param <T>    The entity type.
     * @return A {@link com.buschmais.xo.api.ResultIterable}.
     */
    private <T> ResultIterable<T> findByExample(Class<?> type, Map<PrimitivePropertyMethodMetadata<PropertyMetadata>, Object> entity) {
        EntityTypeMetadata<EntityMetadata> entityTypeMetadata = sessionContext.getMetadataProvider().getEntityMetadata(type);
        EntityDiscriminator entityDiscriminator = entityTypeMetadata.getDatastoreMetadata().getDiscriminator();
        if (entityDiscriminator == null) {
            throw new XOException("Type " + type.getName() + " has no discriminator (i.e. cannot be identified in datastore).");
        }
        final ResultIterator<Entity> iterator = sessionContext.getDatastoreSession().getDatastoreEntityManager()
                .findEntity(entityTypeMetadata, entityDiscriminator, entity);
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
                        AbstractInstanceManager<EntityId, Entity> entityInstanceManager = sessionContext.getEntityInstanceManager();
                        return entityInstanceManager.readInstance(entity);
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
        }, sessionContext.getXOTransaction());
    }

    @Override
    public CompositeObject create(Class<?> type, Class<?>... types) {
        return createByExample(type, types, emptyMap());
    }

    @Override
    public CompositeObject create(Example<CompositeObject> example, Class<?> type, Class<?>... types) {
        Map<PrimitivePropertyMethodMetadata<PropertyMetadata>, Object> exampleEntity = prepareExample(example, type);
        return createByExample(type, new Class<?>[0], exampleEntity);
    }

    public <T> T create(Class<T> type) {
        return createByExample(type, new Class<?>[0], emptyMap()).as(type);
    }

    @Override
    public <T> T create(Example<T> example, Class<T> type) {
        Map<PrimitivePropertyMethodMetadata<PropertyMetadata>, Object> exampleEntity = prepareExample(example, type);
        return createByExample(type, new Class<?>[0], exampleEntity).as(type);
    }

    /**
     * Create a new {@link CompositeObject} instance using an example.
     *
     * @param type          The interface the property type shall implement.
     * @param types         Additional interfaces the entity type shall implement.
     * @param exampleEntity The example instance.
     * @return The {@link CompositeObject} instance.
     */
    private CompositeObject createByExample(Class<?> type, Class<?>[] types, Map<PrimitivePropertyMethodMetadata<PropertyMetadata>, Object> exampleEntity) {
        TypeMetadataSet<EntityTypeMetadata<EntityMetadata>> effectiveTypes = getEffectiveTypes(type, types);
        Set<EntityDiscriminator> entityDiscriminators = sessionContext.getMetadataProvider().getEntityDiscriminators(effectiveTypes);
        DatastoreSession<EntityId, Entity, EntityMetadata, EntityDiscriminator, RelationId, Relation, RelationMetadata, RelationDiscriminator, PropertyMetadata> datastoreSession = sessionContext
                .getDatastoreSession();
        Entity entity = datastoreSession.getDatastoreEntityManager().createEntity(effectiveTypes, entityDiscriminators, exampleEntity);
        AbstractInstanceManager<EntityId, Entity> entityInstanceManager = sessionContext.getEntityInstanceManager();
        CompositeObject instance = entityInstanceManager.createInstance(entity);
        sessionContext.getInstanceListenerService().postCreate(instance);
        return instance;
    }

    @Override
    public <S, R, T> R create(S from, Class<R> relationType, T to) {
        MetadataProvider<EntityMetadata, EntityDiscriminator, RelationMetadata, RelationDiscriminator> metadataProvider = sessionContext.getMetadataProvider();
        AbstractRelationPropertyMethodMetadata<?> fromProperty = metadataProvider.getPropertyMetadata(from.getClass(), relationType, FROM);
        AbstractRelationPropertyMethodMetadata<?> toProperty = metadataProvider.getPropertyMetadata(to.getClass(), relationType, TO);
        Entity entity = sessionContext.getEntityInstanceManager().getDatastoreType(from);
        R instance = sessionContext.getEntityPropertyManager().createRelationReference(entity, fromProperty, to, toProperty);
        sessionContext.getInstanceListenerService().postCreate(instance);
        return instance;
    }

    @Override
    public <T, Id> Id getId(T instance) {
        AbstractInstanceManager<EntityId, Entity> entityInstanceManager = sessionContext.getEntityInstanceManager();
        AbstractInstanceManager<RelationId, Relation> relationInstanceManager = sessionContext.getRelationInstanceManager();
        if (entityInstanceManager.isInstance(instance)) {
            Entity entity = entityInstanceManager.getDatastoreType(instance);
            return (Id) sessionContext.getDatastoreSession().getDatastoreEntityManager().getEntityId(entity);
        } else if (relationInstanceManager.isInstance(instance)) {
            Relation relation = relationInstanceManager.getDatastoreType(instance);
            return (Id) sessionContext.getDatastoreSession().getDatastoreRelationManager().getRelationId(relation);
        }
        throw new XOException(instance + " is not a managed XO instance.");
    }

    @Override
    public <T, M> CompositeObject migrate(T instance, MigrationStrategy<T, M> migrationStrategy, Class<M> targetType, Class<?>... targetTypes) {
        AbstractInstanceManager<EntityId, Entity> entityInstanceManager = sessionContext.getEntityInstanceManager();
        Entity entity = entityInstanceManager.getDatastoreType(instance);
        DatastoreSession<EntityId, Entity, EntityMetadata, EntityDiscriminator, RelationId, Relation, RelationMetadata, RelationDiscriminator, PropertyMetadata> datastoreSession = sessionContext
                .getDatastoreSession();
        Set<EntityDiscriminator> entityDiscriminators = datastoreSession.getDatastoreEntityManager().getEntityDiscriminators(entity);
        MetadataProvider<EntityMetadata, EntityDiscriminator, RelationMetadata, RelationDiscriminator> metadataProvider = sessionContext.getMetadataProvider();
        TypeMetadataSet<EntityTypeMetadata<EntityMetadata>> types = metadataProvider.getTypes(entityDiscriminators);
        TypeMetadataSet<EntityTypeMetadata<EntityMetadata>> effectiveTargetTypes = getEffectiveTypes(targetType, targetTypes);
        Set<EntityDiscriminator> targetEntityDiscriminators = metadataProvider.getEntityDiscriminators(effectiveTargetTypes);
        datastoreSession.getDatastoreEntityManager().migrateEntity(entity, types, entityDiscriminators, effectiveTargetTypes, targetEntityDiscriminators);
        entityInstanceManager.removeInstance(instance);
        CompositeObject migratedInstance = entityInstanceManager.updateInstance(entity);
        if (migrationStrategy != null) {
            migrationStrategy.migrate(instance, migratedInstance.as(targetType));
        }
        entityInstanceManager.closeInstance(instance);
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
        AbstractInstanceManager<EntityId, Entity> entityInstanceManager = sessionContext.getEntityInstanceManager();
        AbstractInstanceManager<RelationId, Relation> relationInstanceManager = sessionContext.getRelationInstanceManager();
        DatastoreSession<EntityId, Entity, EntityMetadata, EntityDiscriminator, RelationId, Relation, RelationMetadata, RelationDiscriminator, PropertyMetadata> datastoreSession = sessionContext
                .getDatastoreSession();
        if (entityInstanceManager.isInstance(instance)) {
            Entity entity = entityInstanceManager.getDatastoreType(instance);
            sessionContext.getInstanceListenerService().preDelete(instance);
            datastoreSession.getDatastoreEntityManager().deleteEntity(entity);
            entityInstanceManager.removeInstance(instance);
            entityInstanceManager.closeInstance(instance);
            sessionContext.getInstanceListenerService().postDelete(instance);
        } else if (relationInstanceManager.isInstance(instance)) {
            Relation relation = relationInstanceManager.getDatastoreType(instance);
            sessionContext.getInstanceListenerService().preDelete(instance);
            datastoreSession.getDatastoreRelationManager().deleteRelation(relation);
            relationInstanceManager.removeInstance(instance);
            relationInstanceManager.closeInstance(instance);
            sessionContext.getInstanceListenerService().postDelete(instance);
        } else {
            throw new XOException(instance + " is not a managed XO instance.");
        }
    }

    @Override
    public Query<CompositeRowObject> createQuery(String query) {
        XOQueryImpl<CompositeRowObject, ?, String, Entity, Relation> xoQuery = new XOQueryImpl<>(sessionContext, query);
        return sessionContext.getInterceptorFactory().addInterceptor(xoQuery);
    }

    @Override
    public <T> Query<T> createQuery(String query, Class<T> type) {
        XOQueryImpl<T, ?, String, Entity, Relation> xoQuery = new XOQueryImpl<>(sessionContext, query, type);
        return sessionContext.getInterceptorFactory().addInterceptor(xoQuery);
    }

    @Override
    public Query<CompositeRowObject> createQuery(String query, Class<?> type, Class<?>... types) {
        XOQueryImpl<CompositeRowObject, ?, String, Entity, Relation> xoQuery = new XOQueryImpl<>(sessionContext, query, type, Arrays.asList(types));
        return sessionContext.getInterceptorFactory().addInterceptor(xoQuery);
    }

    @Override
    public <T> Query<T> createQuery(Class<T> query) {
        XOQueryImpl<T, ?, Class<T>, Entity, Relation> xoQuery = new XOQueryImpl<>(sessionContext, query, query);
        return sessionContext.getInterceptorFactory().addInterceptor(xoQuery);
    }

    @Override
    public <Q> Query<CompositeRowObject> createQuery(Class<Q> query, Class<?>... types) {
        XOQueryImpl<CompositeRowObject, ?, Class<Q>, Entity, Relation> xoQuery = new XOQueryImpl<>(sessionContext, query, query, Arrays.asList(types));
        return sessionContext.getInterceptorFactory().addInterceptor(xoQuery);
    }

    @Override
    public void close() {
        sessionContext.getEntityInstanceManager().close();
        sessionContext.getRelationInstanceManager().close();
        sessionContext.getDatastoreSession().close();
    }

    @Override
    public <DS> DS getDatastoreSession(Class<DS> sessionType) {
        DatastoreSession<EntityId, Entity, EntityMetadata, EntityDiscriminator, RelationId, Relation, RelationMetadata, RelationDiscriminator, PropertyMetadata> datastoreSession = sessionContext
                .getDatastoreSession();
        return sessionType.cast(datastoreSession);
    }

    @Override
    public void flush() {
        sessionContext.getCacheSynchronizationService().flush();
    }

    @Override
    public <I> void registerInstanceListener(I instanceListener) {
        sessionContext.getInstanceListenerService().registerInstanceListener(instanceListener);
    }

    private TypeMetadataSet<EntityTypeMetadata<EntityMetadata>> getEffectiveTypes(Class<?> type, Class<?>... types) {
        MetadataProvider<EntityMetadata, EntityDiscriminator, RelationMetadata, RelationDiscriminator> metadataProvider = sessionContext.getMetadataProvider();
        TypeMetadataSet<EntityTypeMetadata<EntityMetadata>> effectiveTypes = new TypeMetadataSet<>();
        effectiveTypes.add(metadataProvider.getEntityMetadata(type));
        for (Class<?> otherType : types) {
            effectiveTypes.add(metadataProvider.getEntityMetadata(otherType));
        }
        return effectiveTypes;
    }
}
