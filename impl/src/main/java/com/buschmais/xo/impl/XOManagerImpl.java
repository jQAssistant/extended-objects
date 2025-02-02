package com.buschmais.xo.impl;

import java.util.*;

import javax.validation.ConstraintViolation;

import com.buschmais.xo.api.*;
import com.buschmais.xo.api.metadata.MetadataProvider;
import com.buschmais.xo.api.metadata.method.AbstractRelationPropertyMethodMetadata;
import com.buschmais.xo.api.metadata.method.IndexedPropertyMethodMetadata;
import com.buschmais.xo.api.metadata.method.PrimitivePropertyMethodMetadata;
import com.buschmais.xo.api.metadata.type.*;
import com.buschmais.xo.impl.instancelistener.InstanceListenerService;
import com.buschmais.xo.impl.proxy.InstanceInvocationHandler;
import com.buschmais.xo.impl.proxy.example.ExampleProxyMethodService;
import com.buschmais.xo.impl.proxy.repository.RepositoryInvocationHandler;
import com.buschmais.xo.impl.proxy.repository.RepositoryProxyMethodService;
import com.buschmais.xo.impl.query.XOQueryImpl;
import com.buschmais.xo.spi.datastore.DatastoreRelationManager;
import com.buschmais.xo.spi.datastore.DatastoreSession;
import com.buschmais.xo.spi.session.InstanceManager;
import com.buschmais.xo.spi.session.XOSession;

import static com.buschmais.xo.api.Query.Result.CompositeRowObject;
import static com.buschmais.xo.api.metadata.type.RelationTypeMetadata.Direction.FROM;
import static com.buschmais.xo.api.metadata.type.RelationTypeMetadata.Direction.TO;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;

/**
 * Generic implementation of a {@link com.buschmais.xo.api.XOManager}.
 *
 * @param <EntityId>
 *     The type of entity ids as provided by the datastore.
 * @param <Entity>
 *     The type entities as provided by the datastore.
 * @param <EntityMetadata>
 *     The type of entity metadata as provided by the datastore.
 * @param <EntityDiscriminator>
 *     The type of discriminators as provided by the datastore.
 * @param <RelationId>
 *     The type of relation ids as provided by the datastore.
 * @param <Relation>
 *     The type of relations as provided by the datastore.
 * @param <RelationMetadata>
 *     The type of relation metadata as provided by the datastore.
 * @param <RelationDiscriminator>
 *     The type of relation discriminators as provided by the datastore.
 * @param <PropertyMetadata>
 *     The type of property metadata as provided by the datastore.
 */
public class XOManagerImpl<EntityId, Entity, EntityMetadata extends DatastoreEntityMetadata<EntityDiscriminator>, EntityDiscriminator, RelationId, Relation, RelationMetadata extends DatastoreRelationMetadata<RelationDiscriminator>, RelationDiscriminator, PropertyMetadata>
    implements XOManager {

    private final SessionContext<EntityId, Entity, EntityMetadata, EntityDiscriminator, RelationId, Relation, RelationMetadata, RelationDiscriminator, PropertyMetadata> sessionContext;

    private final XOSession<EntityMetadata, EntityDiscriminator, RelationMetadata, RelationDiscriminator> session;

    private final Map<Class<?>, Object> repositories = new HashMap<>();

    private final Map<Class<?>, ExampleProxyMethodService<?>> exampleProxyMethodServices = new HashMap<>();

    private final DefaultCloseSupport closeSupport = new DefaultCloseSupport();

    /**
     * Constructor.
     *
     * @param sessionContext
     *     The associated {@link SessionContext}.
     */
    public XOManagerImpl(
        SessionContext<EntityId, Entity, EntityMetadata, EntityDiscriminator, RelationId, Relation, RelationMetadata, RelationDiscriminator, PropertyMetadata> sessionContext) {
        this.sessionContext = sessionContext;
        this.session = new XOSessionImpl<>(sessionContext);
    }

    @Override
    public XOTransaction currentTransaction() {
        return sessionContext.getXOTransaction();
    }

    @Override
    public Set<ConstraintViolation<Object>> validate() {
        return sessionContext.getInstanceValidationService()
            .validate();
    }

    @Override
    public <T, I> T findById(Class<T> type, I id) {
        TypeMetadata typeMetadata = sessionContext.getMetadataProvider()
            .getRegisteredMetadata()
            .get(type);
        if (typeMetadata == null) {
            throw new XOException(type.getName() + " is not a registered type.");
        } else if (typeMetadata instanceof SimpleTypeMetadata) {
            throw new XOException(type.getName() + " must either be an entity or relation type.");
        } else if (typeMetadata instanceof EntityTypeMetadata) {
            EntityTypeMetadata<EntityMetadata> entityTypeMetadata = (EntityTypeMetadata<EntityMetadata>) typeMetadata;
            Entity entityById = sessionContext.getDatastoreSession()
                .getDatastoreEntityManager()
                .findEntityById(entityTypeMetadata, entityTypeMetadata.getDatastoreMetadata()
                    .getDiscriminator(), (EntityId) id);
            return sessionContext.getEntityInstanceManager()
                .readInstance(entityById);
        } else if (typeMetadata instanceof RelationTypeMetadata) {
            RelationTypeMetadata<RelationMetadata> relationTypeMetadata = (RelationTypeMetadata<RelationMetadata>) typeMetadata;
            Relation relationById = sessionContext.getDatastoreSession()
                .getDatastoreRelationManager()
                .findRelationById(relationTypeMetadata, (RelationId) id);
            return sessionContext.getRelationInstanceManager()
                .readInstance(relationById);
        }
        throw new XOException("Unsupported metadata type: " + typeMetadata);
    }

    @Override
    public <T> ResultIterable<T> find(final Class<T> type, final Object value) {
        sessionContext.getCacheSynchronizationService()
            .flush();
        EntityTypeMetadata<EntityMetadata> entityTypeMetadata = sessionContext.getMetadataProvider()
            .getEntityMetadata(type);
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

    @Override
    public <T> ResultIterable<T> find(Class<T> type, Example<T> example) {
        Map<PrimitivePropertyMethodMetadata<PropertyMetadata>, Object> exampleEntity = prepareExample(example, type);
        return findByExample(type, exampleEntity);
    }

    /**
     * Setup an example entity.
     *
     * @param type
     *     The type.
     * @param example
     *     The provided example.
     * @param <T>
     *     The type.
     * @return The example.
     */
    private <T> Map<PrimitivePropertyMethodMetadata<PropertyMetadata>, Object> prepareExample(Example<T> example, Class<?> type, Class<?>... types) {
        Map<PrimitivePropertyMethodMetadata<PropertyMetadata>, Object> exampleEntity = new HashMap<>();
        ExampleProxyMethodService<T> proxyMethodService = (ExampleProxyMethodService<T>) exampleProxyMethodServices.computeIfAbsent(type,
            k -> new ExampleProxyMethodService(type, sessionContext));
        InstanceInvocationHandler invocationHandler = new InstanceInvocationHandler(exampleEntity, proxyMethodService);
        List<Class<?>> effectiveTypes = new ArrayList<>(types.length + 1);
        effectiveTypes.add(type);
        effectiveTypes.addAll(asList(types));
        CompositeType compositeType = CompositeType.builder()
            .type(CompositeObject.class)
            .type(type)
            .types(types)
            .build();
        T instance = sessionContext.getProxyFactory()
            .createInstance(invocationHandler, compositeType);
        example.prepare(instance);
        return exampleEntity;
    }

    /**
     * Find entities according to the given example entity.
     *
     * @param type
     *     The entity type.
     * @param entity
     *     The example entity.
     * @param <T>
     *     The entity type.
     * @return A {@link ResultIterable}.
     */
    private <T> ResultIterable<T> findByExample(Class<?> type, Map<PrimitivePropertyMethodMetadata<PropertyMetadata>, Object> entity) {
        sessionContext.getCacheSynchronizationService()
            .flush();
        EntityTypeMetadata<EntityMetadata> entityTypeMetadata = sessionContext.getMetadataProvider()
            .getEntityMetadata(type);
        EntityDiscriminator entityDiscriminator = entityTypeMetadata.getDatastoreMetadata()
            .getDiscriminator();
        if (entityDiscriminator == null) {
            throw new XOException("Type " + type.getName() + " has no discriminator (i.e. cannot be identified in datastore).");
        }
        ResultIterator<Entity> iterator = sessionContext.getDatastoreSession()
            .getDatastoreEntityManager()
            .findEntity(entityTypeMetadata, entityDiscriminator, entity);
        return session.toResult(iterator);
    }

    @Override
    public CompositeObject create(Class<?> type, Class<?>... types) {
        return createByExample(emptyMap(), type, types);
    }

    @Override
    public <T> T create(Class<T> type, Example<T> example, Class<?>... types) {
        Map<PrimitivePropertyMethodMetadata<PropertyMetadata>, Object> exampleEntity = prepareExample(example, type);
        return createByExample(exampleEntity, type, types).as(type);
    }

    public <T> T create(Class<T> type) {
        return createByExample(emptyMap(), type).as(type);
    }

    /**
     * Create a new {@link CompositeObject} instance using an example.
     *
     * @param exampleEntity
     *     The example instance.
     * @param type
     *     The interface the property type shall implement.
     * @param types
     *     Additional interfaces the entity type shall implement.
     * @return The {@link CompositeObject} instance.
     */
    private CompositeObject createByExample(Map<PrimitivePropertyMethodMetadata<PropertyMetadata>, Object> exampleEntity, Class<?> type, Class<?>... types) {
        CompositeTypeMetadata<EntityTypeMetadata<EntityMetadata>> effectiveTypes = sessionContext.getMetadataProvider()
            .getEffectiveTypes(type, types);
        Set<EntityDiscriminator> entityDiscriminators = sessionContext.getMetadataProvider()
            .getEntityDiscriminators(effectiveTypes);
        DatastoreSession<EntityId, Entity, EntityMetadata, EntityDiscriminator, RelationId, Relation, RelationMetadata, RelationDiscriminator, PropertyMetadata> datastoreSession = sessionContext.getDatastoreSession();
        Entity entity = datastoreSession.getDatastoreEntityManager()
            .createEntity(effectiveTypes, entityDiscriminators, exampleEntity);
        AbstractInstanceManager<EntityId, Entity> entityInstanceManager = sessionContext.getEntityInstanceManager();
        CompositeObject instance = entityInstanceManager.createInstance(entity, effectiveTypes);
        sessionContext.getInstanceListenerService()
            .postCreate(instance);
        return instance;
    }

    @Override
    public <S, R, T> R create(S from, Class<R> relationType, T to) {
        return createByExample(from, relationType, to, Collections.emptyMap());
    }

    @Override
    public <S, R, T> R create(S from, Class<R> relationType, T to, Example<R> example) {
        Map<PrimitivePropertyMethodMetadata<PropertyMetadata>, Object> exampleRelation = prepareExample(example, relationType);
        return createByExample(from, relationType, to, exampleRelation);
    }

    private <S, R, T> R createByExample(S from, Class<R> relationType, T to, Map<PrimitivePropertyMethodMetadata<PropertyMetadata>, Object> example) {
        MetadataProvider<EntityMetadata, EntityDiscriminator, RelationMetadata, RelationDiscriminator> metadataProvider = sessionContext.getMetadataProvider();
        AbstractRelationPropertyMethodMetadata<?> fromProperty = metadataProvider.getPropertyMetadata(from.getClass(), relationType, FROM);
        AbstractRelationPropertyMethodMetadata<?> toProperty = metadataProvider.getPropertyMetadata(to.getClass(), relationType, TO);
        Entity entity = sessionContext.getEntityInstanceManager()
            .getDatastoreType(from);
        R instance = sessionContext.getEntityPropertyManager()
            .createRelationReference(entity, fromProperty, to, toProperty, example);
        sessionContext.getInstanceListenerService()
            .postCreate(instance);
        return instance;
    }

    @Override
    public <T> T getRepository(Class<T> repositoryType) {
        return (T) repositories.computeIfAbsent(repositoryType, this::createRepository);
    }

    private <T> T createRepository(Class<T> repositoryType) {
        T datastoreRepository = sessionContext.getDatastoreSession()
            .createRepository(session, repositoryType);
        RepositoryProxyMethodService<T, Entity, Relation> proxyMethodService;
        if (repositoryType.isAssignableFrom(datastoreRepository.getClass())) {
            proxyMethodService = new RepositoryProxyMethodService<>(datastoreRepository, repositoryType);
        } else {
            RepositoryTypeMetadata repositoryMetadata = sessionContext.getMetadataProvider()
                .getRepositoryMetadata(repositoryType);
            proxyMethodService = new RepositoryProxyMethodService<>(datastoreRepository, repositoryMetadata, sessionContext);
        }
        RepositoryInvocationHandler invocationHandler = new RepositoryInvocationHandler(proxyMethodService, this);
        T instance = sessionContext.getProxyFactory()
            .createInstance(invocationHandler, CompositeType.builder()
                .type(repositoryType)
                .build());
        return sessionContext.getInterceptorFactory()
            .addInterceptor(instance, repositoryType);
    }

    @Override
    public <T, Id> Id getId(T instance) {
        InstanceManager<EntityId, Entity> entityInstanceManager = sessionContext.getEntityInstanceManager();
        InstanceManager<RelationId, Relation> relationInstanceManager = sessionContext.getRelationInstanceManager();
        if (entityInstanceManager.isInstance(instance)) {
            Entity entity = entityInstanceManager.getDatastoreType(instance);
            return (Id) sessionContext.getDatastoreSession()
                .getDatastoreEntityManager()
                .getEntityId(entity);
        } else if (relationInstanceManager.isInstance(instance)) {
            Relation relation = relationInstanceManager.getDatastoreType(instance);
            return (Id) sessionContext.getDatastoreSession()
                .getDatastoreRelationManager()
                .getRelationId(relation);
        }
        throw new XOException(instance + " is not a managed XO instance.");
    }

    @Override
    public <T> XOMigrator migrate(T instance) {
        return sessionContext.getInterceptorFactory()
            .addInterceptor(new XOMigratorImpl<>(instance, sessionContext));
    }

    @Override
    public <T> void delete(T instance) {
        InstanceListenerService instanceListenerService = sessionContext.getInstanceListenerService();
        InstanceManager<EntityId, Entity> entityInstanceManager = sessionContext.getEntityInstanceManager();
        InstanceManager<RelationId, Relation> relationInstanceManager = sessionContext.getRelationInstanceManager();
        DatastoreSession<EntityId, Entity, EntityMetadata, EntityDiscriminator, RelationId, Relation, RelationMetadata, RelationDiscriminator, PropertyMetadata> datastoreSession = sessionContext.getDatastoreSession();
        if (entityInstanceManager.isInstance(instance)) {
            Entity entity = entityInstanceManager.getDatastoreType(instance);
            instanceListenerService.preDelete(instance);
            datastoreSession.getDatastoreEntityManager()
                .deleteEntity(entity);
            entityInstanceManager.removeInstance(instance);
            entityInstanceManager.closeInstance(instance);
            instanceListenerService.postDelete(instance);
        } else if (relationInstanceManager.isInstance(instance)) {
            DatastoreRelationManager<Entity, RelationId, Relation, RelationMetadata, RelationDiscriminator, PropertyMetadata> datastoreRelationManager = datastoreSession.getDatastoreRelationManager();
            Relation relation = relationInstanceManager.getDatastoreType(instance);
            Entity from = datastoreRelationManager.getFrom(relation);
            Entity to = datastoreRelationManager.getTo(relation);
            instanceListenerService.preDelete(instance);
            datastoreRelationManager.getRelationDiscriminator(relation);
            datastoreRelationManager.deleteRelation(relation);
            relationInstanceManager.removeInstance(instance);
            relationInstanceManager.closeInstance(instance);
            entityInstanceManager.updateInstance(from);
            entityInstanceManager.updateInstance(to);
            instanceListenerService.postDelete(instance);
        } else {
            throw new XOException(instance + " is not a managed XO instance.");
        }
    }

    @Override
    public Query<CompositeRowObject> createQuery(String query) {
        XOQueryImpl<CompositeRowObject, ?, String, Entity, Relation> xoQuery = new XOQueryImpl<>(sessionContext, query);
        return sessionContext.getInterceptorFactory()
            .addInterceptor(xoQuery, Query.class);
    }

    @Override
    public <T> Query<T> createQuery(String query, Class<T> type) {
        XOQueryImpl<T, ?, String, Entity, Relation> xoQuery = new XOQueryImpl<>(sessionContext, query, type);
        return sessionContext.getInterceptorFactory()
            .addInterceptor(xoQuery, Query.class);
    }

    @Override
    public <T> Query<T> createQuery(Class<T> query) {
        XOQueryImpl<T, ?, Class<T>, Entity, Relation> xoQuery = new XOQueryImpl<>(sessionContext, query, query);
        return sessionContext.getInterceptorFactory()
            .addInterceptor(xoQuery, Query.class);
    }

    @Override
    public void close() {
        fireOnBeforeClose();
        sessionContext.getEntityInstanceManager()
            .close();
        sessionContext.getRelationInstanceManager()
            .close();
        sessionContext.getDatastoreSession()
            .close();
        fireOnAfterClose();
    }

    @Override
    public <DS> DS getDatastoreSession(Class<DS> sessionType) {
        DatastoreSession<EntityId, Entity, EntityMetadata, EntityDiscriminator, RelationId, Relation, RelationMetadata, RelationDiscriminator, PropertyMetadata> datastoreSession = sessionContext.getDatastoreSession();
        return sessionType.cast(datastoreSession);
    }

    @Override
    public void flush() {
        sessionContext.getCacheSynchronizationService()
            .flush();
    }

    @Override
    public void clear() {
        sessionContext.getCacheSynchronizationService()
            .clear();
    }

    @Override
    public <I> void registerInstanceListener(I instanceListener) {
        sessionContext.getInstanceListenerService()
            .registerInstanceListener(instanceListener);
    }

    @Override
    public void addCloseListener(CloseListener listener) {
        closeSupport.addCloseListener(listener);
    }

    @Override
    public void removeCloseListener(CloseListener listener) {
        closeSupport.removeCloseListener(listener);
    }

    private void fireOnBeforeClose() {
        closeSupport.fireOnBeforeClose();
    }

    private void fireOnAfterClose() {
        closeSupport.fireOnAfterClose();
    }

}
