package com.buschmais.xo.impl;

import com.buschmais.xo.api.*;
import com.buschmais.xo.impl.query.XOQueryImpl;
import com.buschmais.xo.impl.transaction.TransactionalResultIterable;
import com.buschmais.xo.spi.datastore.DatastoreEntityMetadata;
import com.buschmais.xo.spi.datastore.DatastoreRelationMetadata;
import com.buschmais.xo.spi.datastore.DatastoreSession;
import com.buschmais.xo.spi.datastore.TypeMetadataSet;
import com.buschmais.xo.spi.metadata.method.AbstractRelationPropertyMethodMetadata;
import com.buschmais.xo.spi.metadata.type.EntityTypeMetadata;

import javax.validation.ConstraintViolation;

import java.util.Arrays;
import java.util.Set;

import static com.buschmais.xo.api.Query.Result.CompositeRowObject;
import static com.buschmais.xo.spi.metadata.type.RelationTypeMetadata.Direction.FROM;
import static com.buschmais.xo.spi.metadata.type.RelationTypeMetadata.Direction.TO;

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
 */
public class XOManagerImpl<EntityId, Entity, EntityMetadata extends DatastoreEntityMetadata<EntityDiscriminator>, EntityDiscriminator, RelationId, Relation, RelationMetadata extends DatastoreRelationMetadata<RelationDiscriminator>, RelationDiscriminator> implements XOManager {

    private final SessionContext<EntityId, Entity, EntityMetadata, EntityDiscriminator, RelationId, Relation, RelationMetadata, RelationDiscriminator> sessionContext;

    /**
     * Constructor.
     *
     * @param sessionContext The associated {@link SessionContext}.
     */
    public XOManagerImpl(final SessionContext<EntityId, Entity, EntityMetadata, EntityDiscriminator, RelationId, Relation, RelationMetadata, RelationDiscriminator> sessionContext) {
        this.sessionContext = sessionContext;
    }

    @Override
    public XOTransaction currentTransaction() {
        return sessionContext.getXOTransaction();
    }

    @Override
    public Set<ConstraintViolation<Object>> validate() {
        return sessionContext.getInstanceValidationService().validate();
    }

    @Override
    public <T> ResultIterable<T> find(final Class<T> type, final Object value) {
        final EntityTypeMetadata<EntityMetadata> entityTypeMetadata = sessionContext.getMetadataProvider().getEntityMetadata(type);
        final EntityDiscriminator entityDiscriminator = entityTypeMetadata.getDatastoreMetadata().getDiscriminator();
        if (entityDiscriminator == null) {
            throw new XOException("Type " + type.getName() + " has no discriminator (i.e. cannot be identified in datastore).");
        }
        final ResultIterator<Entity> iterator = sessionContext.getDatastoreSession().findEntity(entityTypeMetadata, entityDiscriminator, value);
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
                        final Entity entity = iterator.next();
                        final AbstractInstanceManager<EntityId, Entity> entityInstanceManager = sessionContext.getEntityInstanceManager();
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
    public CompositeObject create(final Class type, final Class<?>... types) {
        final TypeMetadataSet<EntityTypeMetadata<EntityMetadata>> effectiveTypes = getEffectiveTypes(type, types);
        final Set<EntityDiscriminator> entityDiscriminators = sessionContext.getMetadataProvider().getEntityDiscriminators(effectiveTypes);
        final DatastoreSession<EntityId, Entity, EntityMetadata, EntityDiscriminator, RelationId, Relation, RelationMetadata, RelationDiscriminator> datastoreSession = sessionContext.getDatastoreSession();
        final Entity entity = datastoreSession.createEntity(effectiveTypes, entityDiscriminators);
        final AbstractInstanceManager<EntityId, Entity> entityInstanceManager = sessionContext.getEntityInstanceManager();
        final CompositeObject instance = entityInstanceManager.createInstance(entity);
        sessionContext.getInstanceListenerService().postCreate(instance);
        return instance;
    }

    @Override
    public <T> T create(final Class<T> type) {
        return create(type, new Class<?>[0]).as(type);
    }

    @Override
    public <S, R, T> R create(final S from, final Class<R> relationType, final T to) {
        final MetadataProvider<EntityMetadata, EntityDiscriminator, RelationMetadata, RelationDiscriminator> metadataProvider = sessionContext.getMetadataProvider();
        final AbstractRelationPropertyMethodMetadata<?> fromProperty = metadataProvider.getPropertyMetadata(from.getClass(), relationType, FROM);
        final AbstractRelationPropertyMethodMetadata<?> toProperty = metadataProvider.getPropertyMetadata(to.getClass(), relationType, TO);
        final Entity entity = sessionContext.getEntityInstanceManager().getDatastoreType(from);
        final R instance = sessionContext.getEntityPropertyManager().createRelationReference(entity, fromProperty, to, toProperty);
        sessionContext.getInstanceListenerService().postCreate(instance);
        return instance;
    }

    @Override
    public <T, Id> Id getId(final T instance) {
        final AbstractInstanceManager<EntityId, Entity> entityInstanceManager = sessionContext.getEntityInstanceManager();
        final AbstractInstanceManager<RelationId, Relation> relationInstanceManager = sessionContext.getRelationInstanceManager();
        if (entityInstanceManager.isInstance(instance)) {
            final Entity entity = entityInstanceManager.getDatastoreType(instance);
            return (Id) sessionContext.getDatastoreSession().getEntityId(entity);
        } else if (relationInstanceManager.isInstance(instance)) {
            final Relation relation = relationInstanceManager.getDatastoreType(instance);
            return (Id) sessionContext.getDatastoreSession().getRelationId(relation);
        }
        throw new XOException(instance + " is not a managed XO instance.");
    }

    @Override
    public <T, M> CompositeObject migrate(final T instance, final MigrationStrategy<T, M> migrationStrategy, final Class<M> targetType, final Class<?>... targetTypes) {
        final AbstractInstanceManager<EntityId, Entity> entityInstanceManager = sessionContext.getEntityInstanceManager();
        final Entity entity = entityInstanceManager.getDatastoreType(instance);
        final DatastoreSession<EntityId, Entity, EntityMetadata, EntityDiscriminator, RelationId, Relation, RelationMetadata, RelationDiscriminator> datastoreSession = sessionContext.getDatastoreSession();
        final Set<EntityDiscriminator> entityDiscriminators = datastoreSession.getEntityDiscriminators(entity);
        final MetadataProvider<EntityMetadata, EntityDiscriminator, RelationMetadata, RelationDiscriminator> metadataProvider = sessionContext.getMetadataProvider();
        final TypeMetadataSet<EntityTypeMetadata<EntityMetadata>> types = metadataProvider.getTypes(entityDiscriminators);
        final TypeMetadataSet<EntityTypeMetadata<EntityMetadata>> effectiveTargetTypes = getEffectiveTypes(targetType, targetTypes);
        final Set<EntityDiscriminator> targetEntityDiscriminators = metadataProvider.getEntityDiscriminators(effectiveTargetTypes);
        datastoreSession.migrateEntity(entity, types, entityDiscriminators, effectiveTargetTypes, targetEntityDiscriminators);
        entityInstanceManager.removeInstance(instance);
        final CompositeObject migratedInstance = entityInstanceManager.updateInstance(entity);
        if (migrationStrategy != null) {
            migrationStrategy.migrate(instance, migratedInstance.as(targetType));
        }
        entityInstanceManager.closeInstance(instance);
        return migratedInstance;
    }

    @Override
    public <T, M> CompositeObject migrate(final T instance, final Class<M> targetType, final Class<?>... targetTypes) {
        return migrate(instance, null, targetType, targetTypes);
    }

    @Override
    public <T, M> M migrate(final T instance, final MigrationStrategy<T, M> migrationStrategy, final Class<M> targetType) {
        return migrate(instance, migrationStrategy, targetType, new Class<?>[0]).as(targetType);
    }

    @Override
    public <T, M> M migrate(final T instance, final Class<M> targetType) {
        return migrate(instance, null, targetType);
    }

    @Override
    public <T> void delete(final T instance) {
        final AbstractInstanceManager<EntityId, Entity> entityInstanceManager = sessionContext.getEntityInstanceManager();
        final AbstractInstanceManager<RelationId, Relation> relationInstanceManager = sessionContext.getRelationInstanceManager();
        final DatastoreSession<EntityId, Entity, EntityMetadata, EntityDiscriminator, RelationId, Relation, RelationMetadata, RelationDiscriminator> datastoreSession = sessionContext.getDatastoreSession();
        if (entityInstanceManager.isInstance(instance)) {
            final Entity entity = entityInstanceManager.getDatastoreType(instance);
            sessionContext.getInstanceListenerService().preDelete(instance);
            datastoreSession.deleteEntity(entity);
            entityInstanceManager.removeInstance(instance);
            entityInstanceManager.closeInstance(instance);
            sessionContext.getInstanceListenerService().postDelete(instance);
        } else if (relationInstanceManager.isInstance(instance)) {
            final Relation relation = relationInstanceManager.getDatastoreType(instance);
            sessionContext.getInstanceListenerService().preDelete(instance);
            datastoreSession.getDatastorePropertyManager().deleteRelation(relation);
            relationInstanceManager.removeInstance(instance);
            relationInstanceManager.closeInstance(instance);
            sessionContext.getInstanceListenerService().postDelete(instance);
        } else {
            throw new XOException(instance + " is not a managed XO instance.");
        }
    }

    @Override
    public Query<CompositeRowObject> createQuery(final String query) {
        final XOQueryImpl<CompositeRowObject, String, Entity, Relation> xoQuery = new XOQueryImpl<>(sessionContext, query);
        return sessionContext.getInterceptorFactory().addInterceptor(xoQuery);
    }

    @Override
    public <T> Query<T> createQuery(final String query, final Class<T> type) {
        final XOQueryImpl<T, String, Entity, Relation> xoQuery = new XOQueryImpl<>(sessionContext, query, type);
        return sessionContext.getInterceptorFactory().addInterceptor(xoQuery);
    }

    @Override
    public Query<CompositeRowObject> createQuery(final String query, final Class<?> type, final Class<?>... types) {
        final XOQueryImpl<CompositeRowObject, String, Entity, Relation> xoQuery = new XOQueryImpl<>(sessionContext, query, type, Arrays.asList(types));
        return sessionContext.getInterceptorFactory().addInterceptor(xoQuery);
    }

    @Override
    public <T> Query<T> createQuery(final Class<T> query) {
        final XOQueryImpl<T, Class<T>, Entity, Relation> xoQuery = new XOQueryImpl<>(sessionContext, query, query);
        return sessionContext.getInterceptorFactory().addInterceptor(xoQuery);
    }

    @Override
    public Query<CompositeRowObject> createQuery(final NativeQuery<?> query) {
        final XOQueryImpl<CompositeRowObject, String, Entity, Relation> xoQuery = new XOQueryImpl<>(sessionContext, query);
        return sessionContext.getInterceptorFactory().addInterceptor(xoQuery);
    }

    @Override
    public <T> Query<T> createQuery(final NativeQuery<?> query, final Class<T> type) {
        final XOQueryImpl<T, String, Entity, Relation> xoQuery = new XOQueryImpl<>(sessionContext, query, type);
        return sessionContext.getInterceptorFactory().addInterceptor(xoQuery);
    }

    @Override
    public <Q> Query<CompositeRowObject> createQuery(final Class<Q> query, final Class<?>... types) {
        final XOQueryImpl<CompositeRowObject, Class<Q>, Entity, Relation> xoQuery = new XOQueryImpl<>(sessionContext, query, query, Arrays.asList(types));
        return sessionContext.getInterceptorFactory().addInterceptor(xoQuery);
    }

    @Override
    public void close() {
        sessionContext.getEntityInstanceManager().close();
        sessionContext.getRelationInstanceManager().close();
        sessionContext.getDatastoreSession().close();
    }

    @Override
    public <DS> DS getDatastoreSession(final Class<DS> sessionType) {
        final DatastoreSession<EntityId, Entity, EntityMetadata, EntityDiscriminator, RelationId, Relation, RelationMetadata, RelationDiscriminator> datastoreSession = sessionContext.getDatastoreSession();
        return sessionType.cast(datastoreSession);
    }

    @Override
    public void flush() {
        sessionContext.getCacheSynchronizationService().flush();
    }

    @Override
    public <I> void registerInstanceListener(final I instanceListener) {
        sessionContext.getInstanceListenerService().registerInstanceListener(instanceListener);
    }

    private TypeMetadataSet<EntityTypeMetadata<EntityMetadata>> getEffectiveTypes(final Class<?> type, final Class<?>... types) {
        final MetadataProvider<EntityMetadata, EntityDiscriminator, RelationMetadata, RelationDiscriminator> metadataProvider = sessionContext.getMetadataProvider();
        final TypeMetadataSet<EntityTypeMetadata<EntityMetadata>> effectiveTypes = new TypeMetadataSet<>();
        effectiveTypes.add(metadataProvider.getEntityMetadata(type));
        for (final Class<?> otherType : types) {
            effectiveTypes.add(metadataProvider.getEntityMetadata(otherType));
        }
        return effectiveTypes;
    }
}
