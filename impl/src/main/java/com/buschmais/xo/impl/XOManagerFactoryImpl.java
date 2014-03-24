package com.buschmais.xo.impl;

import com.buschmais.xo.api.*;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.impl.metadata.MetadataProviderImpl;
import com.buschmais.xo.impl.reflection.ClassHelper;
import com.buschmais.xo.spi.bootstrap.XODatastoreProvider;
import com.buschmais.xo.spi.datastore.Datastore;
import com.buschmais.xo.spi.datastore.DatastoreEntityMetadata;
import com.buschmais.xo.spi.datastore.DatastoreRelationMetadata;
import com.buschmais.xo.spi.datastore.DatastoreSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.ValidatorFactory;

public class XOManagerFactoryImpl<EntityId, Entity, EntityMetadata extends DatastoreEntityMetadata<EntityDiscriminator>, EntityDiscriminator, RelationId, Relation, RelationMetadata extends DatastoreRelationMetadata<RelationDiscriminator>, RelationDiscriminator> implements XOManagerFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(XOManagerFactoryImpl.class);

    private final XOUnit XOUnit;
    private final MetadataProvider metadataProvider;
    private final ClassLoader classLoader;
    private final Datastore<?, EntityMetadata, EntityDiscriminator, RelationMetadata, RelationDiscriminator> datastore;
    private final ValidatorFactory validatorFactory;
    private final ConcurrencyMode concurrencyMode;
    private final Transaction.TransactionAttribute defaultTransactionAttribute;

    public XOManagerFactoryImpl(XOUnit XOUnit) {
        this.XOUnit = XOUnit;
        Class<?> providerType = XOUnit.getProvider();
        if (providerType == null) {
            throw new XOException("No provider specified for CDO unit '" + XOUnit.getName() + "'.");
        }
        if (!XODatastoreProvider.class.isAssignableFrom(providerType)) {
            throw new XOException(providerType.getName() + " specified as CDO provider must implement " + XODatastoreProvider.class.getName());
        }
        XODatastoreProvider<EntityMetadata, EntityDiscriminator, RelationMetadata, RelationDiscriminator> XODatastoreProvider = XODatastoreProvider.class.cast(ClassHelper.newInstance(providerType));
        this.datastore = XODatastoreProvider.createDatastore(XOUnit);
        this.concurrencyMode = XOUnit.getConcurrencyMode();
        this.defaultTransactionAttribute = XOUnit.getDefaultTransactionAttribute();
        final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        final ClassLoader parentClassLoader = contextClassLoader != null ? contextClassLoader : XOUnit.getClass().getClassLoader();
        LOGGER.debug("Using class loader '{}'.", parentClassLoader.toString());
        classLoader = new ClassLoader() {
            @Override
            public Class<?> loadClass(String name) throws ClassNotFoundException {
                return parentClassLoader.loadClass(name);
            }
        };
        metadataProvider = new MetadataProviderImpl(XOUnit.getTypes(), datastore);
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
            LOGGER.debug("Cannot find validation provider.", e);
            LOGGER.info("No JSR 303 Bean Validation provider available.");
        }
        return null;
    }

    @Override
    public XOManager createXOManager() {
        DatastoreSession<EntityId, Entity, EntityMetadata, EntityDiscriminator, RelationId, Relation, RelationMetadata, RelationDiscriminator> datastoreSession = datastore.createSession();
        SessionContext<EntityId, Entity, EntityMetadata, EntityDiscriminator, RelationId, Relation, RelationMetadata, RelationDiscriminator> sessionContext = new SessionContext<>(metadataProvider, datastoreSession, validatorFactory, XOUnit.getInstanceListeners(), defaultTransactionAttribute, concurrencyMode, classLoader);
        XOManagerImpl<EntityId, Entity, EntityMetadata, EntityDiscriminator, RelationId, Relation, RelationMetadata, RelationDiscriminator> cdoManager = new XOManagerImpl<>(sessionContext);
        return sessionContext.getInterceptorFactory().addInterceptor(cdoManager);
    }

    @Override
    public void close() {
        datastore.close();
    }

    @Override
    public XOUnit getXOUnit() {
        return XOUnit;
    }
}
