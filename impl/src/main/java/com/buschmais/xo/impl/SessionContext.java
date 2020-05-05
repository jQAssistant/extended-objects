package com.buschmais.xo.impl;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ValidatorFactory;

import com.buschmais.xo.api.XOTransaction;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.impl.cache.CacheSynchronization;
import com.buschmais.xo.impl.cache.CacheSynchronizationService;
import com.buschmais.xo.impl.cache.TransactionalCache;
import com.buschmais.xo.impl.instancelistener.InstanceListenerService;
import com.buschmais.xo.impl.interceptor.ConcurrencyInterceptor;
import com.buschmais.xo.impl.interceptor.TransactionInterceptor;
import com.buschmais.xo.impl.plugin.PluginRepositoryManager;
import com.buschmais.xo.impl.validation.InstanceValidationService;
import com.buschmais.xo.spi.datastore.DatastoreEntityMetadata;
import com.buschmais.xo.spi.datastore.DatastoreRelationMetadata;
import com.buschmais.xo.spi.datastore.DatastoreSession;
import com.buschmais.xo.spi.datastore.DatastoreTransaction;
import com.buschmais.xo.spi.interceptor.InterceptorFactory;
import com.buschmais.xo.spi.interceptor.XOInterceptor;

/**
 * Provides access to all session related services.
 *
 * @param <EntityId>
 *            The type of entity ids.
 * @param <Entity>
 *            The type of entities.
 * @param <EntityMetadata>
 *            The type of entity metadata.
 * @param <EntityDiscriminator>
 *            The type of entity discriminators
 * @param <RelationId>
 *            The type of relation ids.
 * @param <Relation>
 *            The type of relations.
 * @param <RelationMetadata>
 *            The type of relation metadata.
 * @param <RelationDiscriminator>
 *            The type of relation discriminators.
 * @param <PropertyMetadata>
 *            The type of property metadata.
 */
public class SessionContext<EntityId, Entity, EntityMetadata extends DatastoreEntityMetadata<EntityDiscriminator>, EntityDiscriminator, RelationId, Relation, RelationMetadata extends DatastoreRelationMetadata<RelationDiscriminator>, RelationDiscriminator, PropertyMetadata> {

    private final MetadataProvider<EntityMetadata, EntityDiscriminator, RelationMetadata, RelationDiscriminator> metadataProvider;
    private final PluginRepositoryManager pluginRepositoryManager;
    private final AbstractInstanceManager<EntityId, Entity> entityInstanceManager;
    private final AbstractInstanceManager<RelationId, Relation> relationInstanceManager;
    private final InstanceListenerService instanceListenerService;
    private final TransactionalCache<EntityId> entityCache;
    private final TransactionalCache<RelationId> relationCache;
    private final InstanceValidationService instanceValidationService;
    private final CacheSynchronizationService<Entity, Relation> cacheSynchronizationService;
    private final XOTransactionImpl xoTransaction;
    private final EntityPropertyManager<Entity, Relation, PropertyMetadata> entityPropertyManager;
    private final RelationPropertyManager<Entity, Relation> relationPropertyManager;
    private final InterceptorFactory interceptorFactory;
    private final ProxyFactory proxyFactory;
    private final DatastoreSession<EntityId, Entity, EntityMetadata, EntityDiscriminator, RelationId, Relation, RelationMetadata, RelationDiscriminator, PropertyMetadata> datastoreSession;

    public SessionContext(MetadataProvider<EntityMetadata, EntityDiscriminator, RelationMetadata, RelationDiscriminator> metadataProvider,
            PluginRepositoryManager pluginRepositoryManager,
            DatastoreSession<EntityId, Entity, EntityMetadata, EntityDiscriminator, RelationId, Relation, RelationMetadata, RelationDiscriminator, PropertyMetadata> datastoreSession,
            ValidatorFactory validatorFactory, XOUnit xoUnit, ClassLoader classLoader) {
        this.metadataProvider = metadataProvider;
        this.pluginRepositoryManager = pluginRepositoryManager;
        this.datastoreSession = datastoreSession;
        this.entityCache = new TransactionalCache<>();
        this.relationCache = new TransactionalCache<>();
        DatastoreTransaction datastoreTransaction = datastoreSession.getDatastoreTransaction();
        this.xoTransaction = datastoreTransaction != null ? new XOTransactionImpl(datastoreTransaction) : null;
        List<XOInterceptor> interceptorChain = new ArrayList<>();
        interceptorChain.add(new ConcurrencyInterceptor(xoUnit.getConcurrencyMode()));
        interceptorChain.add(new TransactionInterceptor(xoTransaction, xoUnit.getDefaultTransactionAttribute()));
        this.interceptorFactory = new InterceptorFactory(interceptorChain);
        this.proxyFactory = new ProxyFactory(interceptorFactory, classLoader);
        this.instanceListenerService = new InstanceListenerService(xoUnit.getInstanceListeners());
        this.entityPropertyManager = new EntityPropertyManager<>(this);
        this.relationPropertyManager = new RelationPropertyManager<>(this);
        this.relationInstanceManager = new RelationInstanceManager<>(this);
        this.entityInstanceManager = new EntityInstanceManager<>(this);
        this.instanceValidationService = new InstanceValidationService(validatorFactory, relationCache, entityCache);
        this.cacheSynchronizationService = new CacheSynchronizationService<>(this, xoUnit);
        if (xoTransaction != null) {
            // Register default synchronizations.
            xoTransaction.registerDefaultSynchronization(new CacheSynchronization<>(cacheSynchronizationService));
        }
    }

    public MetadataProvider<EntityMetadata, EntityDiscriminator, RelationMetadata, RelationDiscriminator> getMetadataProvider() {
        return metadataProvider;
    }

    public PluginRepositoryManager getPluginRepositoryManager() {
        return pluginRepositoryManager;
    }

    public AbstractInstanceManager<EntityId, Entity> getEntityInstanceManager() {
        return entityInstanceManager;
    }

    public AbstractInstanceManager<RelationId, Relation> getRelationInstanceManager() {
        return relationInstanceManager;
    }

    public InstanceListenerService getInstanceListenerService() {
        return instanceListenerService;
    }

    public TransactionalCache<EntityId> getEntityCache() {
        return entityCache;
    }

    public TransactionalCache<RelationId> getRelationCache() {
        return relationCache;
    }

    public InstanceValidationService getInstanceValidationService() {
        return instanceValidationService;
    }

    public CacheSynchronizationService<Entity, Relation> getCacheSynchronizationService() {
        return cacheSynchronizationService;
    }

    public XOTransaction getXOTransaction() {
        return xoTransaction;
    }

    public EntityPropertyManager<Entity, Relation, PropertyMetadata> getEntityPropertyManager() {
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

    public DatastoreSession<EntityId, Entity, EntityMetadata, EntityDiscriminator, RelationId, Relation, RelationMetadata, RelationDiscriminator, PropertyMetadata> getDatastoreSession() {
        return datastoreSession;
    }

}
