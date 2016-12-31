package com.buschmais.xo.impl;

import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.ValidatorFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.buschmais.xo.api.CloseListener;
import com.buschmais.xo.api.ConcurrencyMode;
import com.buschmais.xo.api.Transaction;
import com.buschmais.xo.api.ValidationMode;
import com.buschmais.xo.api.XOException;
import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.XOManagerFactory;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.impl.metadata.MetadataProviderImpl;
import com.buschmais.xo.impl.plugin.PluginRepositoryManager;
import com.buschmais.xo.impl.plugin.QueryLanguagePluginRepository;
import com.buschmais.xo.spi.bootstrap.XODatastoreProvider;
import com.buschmais.xo.spi.datastore.Datastore;
import com.buschmais.xo.spi.datastore.DatastoreEntityMetadata;
import com.buschmais.xo.spi.datastore.DatastoreRelationMetadata;
import com.buschmais.xo.spi.datastore.DatastoreSession;
import com.buschmais.xo.spi.reflection.ClassHelper;

public class XOManagerFactoryImpl<EntityId, Entity, EntityMetadata extends DatastoreEntityMetadata<EntityDiscriminator>, EntityDiscriminator, RelationId, Relation, RelationMetadata extends DatastoreRelationMetadata<RelationDiscriminator>, RelationDiscriminator, PropertyMetadata> implements XOManagerFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(XOManagerFactoryImpl.class);

    private final XOUnit xoUnit;
    private final MetadataProvider metadataProvider;
    private final ClassLoader classLoader;
    private final Datastore<?, EntityMetadata, EntityDiscriminator, RelationMetadata, RelationDiscriminator> datastore;
    private final PluginRepositoryManager pluginRepositoryManager;
    private final ValidatorFactory validatorFactory;
    private final ValidationMode validationMode;
    private final ConcurrencyMode concurrencyMode;
    private final Transaction.TransactionAttribute defaultTransactionAttribute;

    private final DefaultCloseSupport closeSupport = new DefaultCloseSupport();

    public XOManagerFactoryImpl(XOUnit xoUnit) {
        this.xoUnit = xoUnit;
        Class<?> providerType = xoUnit.getProvider();
        if (providerType == null) {
            throw new XOException("No provider specified for XO unit '" + xoUnit.getName() + "'.");
        }
        if (!XODatastoreProvider.class.isAssignableFrom(providerType)) {
            throw new XOException(providerType.getName() + " specified as XO provider must implement " + XODatastoreProvider.class.getName());
        }
        XODatastoreProvider<EntityMetadata, EntityDiscriminator, RelationMetadata, RelationDiscriminator> XODatastoreProvider = XODatastoreProvider.class.cast(ClassHelper.newInstance(providerType));
        this.datastore = XODatastoreProvider.createDatastore(xoUnit);
        this.pluginRepositoryManager = new PluginRepositoryManager(new QueryLanguagePluginRepository(datastore));
        this.validationMode = xoUnit.getValidationMode();
        this.concurrencyMode = xoUnit.getConcurrencyMode();
        this.defaultTransactionAttribute = xoUnit.getDefaultTransactionAttribute();
        final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        final ClassLoader parentClassLoader = contextClassLoader != null ? contextClassLoader : xoUnit.getClass().getClassLoader();
        LOGGER.debug("Using class loader '{}'.", parentClassLoader.toString());
        classLoader = new ClassLoader() {
            @Override
            public Class<?> loadClass(String name) throws ClassNotFoundException {
                return parentClassLoader.loadClass(name);
            }
        };
        metadataProvider = new MetadataProviderImpl(xoUnit.getTypes(), datastore);
        this.validatorFactory = getValidatorFactory();
        datastore.init(metadataProvider.getRegisteredMetadata());
    }

    /**
     * Return the {@link javax.validation.ValidatorFactory}.
     *
     * @return The {@link javax.validation.ValidatorFactory}.
     */
    private ValidatorFactory getValidatorFactory() {
        try {
            return Validation.buildDefaultValidatorFactory();
        } catch (ValidationException e) {
            LOGGER.debug("No JSR 303 Bean Validation provider available.", e);
        }
        return null;
    }

    @Override
    public XOManager createXOManager() {
        DatastoreSession<EntityId, Entity, EntityMetadata, EntityDiscriminator, RelationId, Relation, RelationMetadata, RelationDiscriminator, PropertyMetadata> datastoreSession = datastore.createSession();
        SessionContext<EntityId, Entity, EntityMetadata, EntityDiscriminator, RelationId, Relation, RelationMetadata, RelationDiscriminator, PropertyMetadata> sessionContext = new SessionContext<>(metadataProvider, pluginRepositoryManager, datastoreSession, validatorFactory, xoUnit.getInstanceListeners(), defaultTransactionAttribute, validationMode, concurrencyMode, classLoader);
        XOManagerImpl<EntityId, Entity, EntityMetadata, EntityDiscriminator, RelationId, Relation, RelationMetadata, RelationDiscriminator, PropertyMetadata> xoManager = new XOManagerImpl<>(sessionContext);
        return sessionContext.getInterceptorFactory().addInterceptor(xoManager, XOManager.class);
    }

    @Override
    public void close() {
        fireOnBeforeClose();
        datastore.close();
        fireOnAfterClose();
    }

    @Override
    public XOUnit getXOUnit() {
        return xoUnit;
    }

    /**
     * Return the instance of the plugin manager repository.
     *
     * @return The plugin manager repository.
     */
    public PluginRepositoryManager getPluginRepositoryManager() {
        return pluginRepositoryManager;
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
