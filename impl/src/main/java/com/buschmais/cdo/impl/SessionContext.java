package com.buschmais.cdo.impl;

import com.buschmais.cdo.api.CdoTransaction;
import com.buschmais.cdo.api.ConcurrencyMode;
import com.buschmais.cdo.impl.cache.CacheSynchronization;
import com.buschmais.cdo.impl.cache.CacheSynchronizationService;
import com.buschmais.cdo.impl.cache.TransactionalCache;
import com.buschmais.cdo.impl.interceptor.InterceptorFactory;
import com.buschmais.cdo.impl.validation.InstanceValidator;
import com.buschmais.cdo.spi.datastore.DatastoreEntityMetadata;
import com.buschmais.cdo.spi.datastore.DatastoreRelationMetadata;
import com.buschmais.cdo.spi.datastore.DatastoreSession;

import javax.validation.ValidatorFactory;

import static com.buschmais.cdo.api.Transaction.TransactionAttribute;

/**
 * Provides access to all session related services.
 *
 * @param <EntityId>              The type of entity ids.
 * @param <Entity>                The type of entities.
 * @param <EntityMetadata>        The type of entity metadata.
 * @param <EntityDiscriminator>   The type of entity discriminators
 * @param <RelationId>            The type of relation ids.
 * @param <Relation>              The type of relations.
 * @param <RelationMetadata>      The type of relation metadata.
 * @param <RelationDiscriminator> The type of relation discriminators.
 */
public class SessionContext<EntityId, Entity, EntityMetadata extends DatastoreEntityMetadata<EntityDiscriminator>, EntityDiscriminator, RelationId, Relation, RelationMetadata extends DatastoreRelationMetadata<RelationDiscriminator>, RelationDiscriminator> {

    private final MetadataProvider<EntityMetadata, EntityDiscriminator, RelationMetadata, RelationDiscriminator> metadataProvider;
    private final AbstractInstanceManager<EntityId, Entity> entityInstanceManager;
    private final AbstractInstanceManager<RelationId, Relation> relationInstanceManager;
    private final TransactionalCache<EntityId> entityCache;
    private final TransactionalCache<RelationId> relationCache;
    private final InstanceValidator instanceValidator;
    private final CacheSynchronizationService<Entity, Relation> cacheSynchronizationService;
    private final CdoTransactionImpl cdoTransaction;
    private final EntityPropertyManager<Entity, Relation> entityPropertyManager;
    private final RelationPropertyManager<Entity, Relation> relationPropertyManager;
    private final InterceptorFactory interceptorFactory;
    private final ProxyFactory proxyFactory;
    private final DatastoreSession<EntityId, Entity, EntityMetadata, EntityDiscriminator, RelationId, Relation, RelationMetadata, RelationDiscriminator> datastoreSession;

    public SessionContext(MetadataProvider<EntityMetadata, EntityDiscriminator, RelationMetadata, RelationDiscriminator> metadataProvider, DatastoreSession<EntityId, Entity, EntityMetadata, EntityDiscriminator, RelationId, Relation, RelationMetadata, RelationDiscriminator> datastoreSession, ValidatorFactory validatorFactory, TransactionAttribute defaultTransactionAttribute, ConcurrencyMode concurrencyMode, ClassLoader classLoader) {
        this.metadataProvider = metadataProvider;
        this.datastoreSession = datastoreSession;
        this.entityCache = new TransactionalCache<>();
        this.relationCache = new TransactionalCache<>();
        this.cdoTransaction = new CdoTransactionImpl(datastoreSession.getDatastoreTransaction());
        this.interceptorFactory = new InterceptorFactory(cdoTransaction, defaultTransactionAttribute, concurrencyMode);
        this.proxyFactory = new ProxyFactory(interceptorFactory, classLoader);
        this.entityPropertyManager = new EntityPropertyManager<>(this);
        this.relationPropertyManager = new RelationPropertyManager<>(this);
        this.relationInstanceManager = new RelationInstanceManager<>(this);
        this.entityInstanceManager = new EntityInstanceManager<>(this);
        this.instanceValidator = new InstanceValidator(validatorFactory, relationCache, entityCache);
        this.cacheSynchronizationService = new CacheSynchronizationService<>(entityCache, entityInstanceManager, relationCache, relationInstanceManager, instanceValidator, datastoreSession);
        // Register default synchronizations.
        cdoTransaction.registerDefaultSynchronization(new CacheSynchronization<>(cacheSynchronizationService, entityCache, relationCache));
    }


    public MetadataProvider<EntityMetadata, EntityDiscriminator, RelationMetadata, RelationDiscriminator> getMetadataProvider() {
        return metadataProvider;
    }

    public AbstractInstanceManager<EntityId, Entity> getEntityInstanceManager() {
        return entityInstanceManager;
    }

    public AbstractInstanceManager<RelationId, Relation> getRelationInstanceManager() {
        return relationInstanceManager;
    }

    public TransactionalCache<EntityId> getEntityCache() {
        return entityCache;
    }

    public TransactionalCache<RelationId> getRelationCache() {
        return relationCache;
    }

    public InstanceValidator getInstanceValidator() {
        return instanceValidator;
    }

    public CacheSynchronizationService<Entity, Relation> getCacheSynchronizationService() {
        return cacheSynchronizationService;
    }

    public CdoTransaction getCdoTransaction() {
        return cdoTransaction;
    }

    public EntityPropertyManager<Entity, Relation> getEntityPropertyManager() {
        return entityPropertyManager;
    }

    public RelationPropertyManager<Entity, Relation> getRelationPropertyManager() {
        return relationPropertyManager;
    }

    public InterceptorFactory getInterceptorFactory() {
        return interceptorFactory;
    }

    public ProxyFactory getProxyFactory() {
        return proxyFactory;
    }

    public DatastoreSession<EntityId, Entity, EntityMetadata, EntityDiscriminator, RelationId, Relation, RelationMetadata, RelationDiscriminator> getDatastoreSession() {
        return datastoreSession;
    }
}
