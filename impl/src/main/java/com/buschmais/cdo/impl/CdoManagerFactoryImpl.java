package com.buschmais.cdo.impl;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.api.CdoManagerFactory;
import com.buschmais.cdo.api.CdoTransaction;
import com.buschmais.cdo.impl.interceptor.InterceptorFactory;
import com.buschmais.cdo.spi.bootstrap.CdoUnit;
import com.buschmais.cdo.impl.cache.CacheSynchronization;
import com.buschmais.cdo.impl.validation.InstanceValidator;
import com.buschmais.cdo.impl.validation.ValidatorSynchronization;
import com.buschmais.cdo.impl.cache.TransactionalCache;
import com.buschmais.cdo.impl.metadata.MetadataProviderImpl;
import com.buschmais.cdo.spi.metadata.MetadataProvider;
import com.buschmais.cdo.spi.datastore.Datastore;
import com.buschmais.cdo.spi.datastore.DatastoreSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.ValidatorFactory;

public class CdoManagerFactoryImpl implements CdoManagerFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(CdoManagerFactoryImpl.class);

    private CdoUnit cdoUnit;
    private MetadataProvider metadataProvider;
    private ClassLoader classLoader;
    private Datastore<?> datastore;
    private ValidatorFactory validatorFactory;
    private TransactionAttribute transactionAttribute;

    public CdoManagerFactoryImpl(CdoUnit cdoUnit, Datastore<?> datastore) {
        this.cdoUnit = cdoUnit;
        this.datastore = datastore;
        this.transactionAttribute = cdoUnit.getTransactionAttribute();
        final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        classLoader = contextClassLoader != null ? contextClassLoader : cdoUnit.getClass().getClassLoader();
        LOGGER.info("Using class loader '{}'.", contextClassLoader.toString());
        classLoader = new ClassLoader() {
            @Override
            public Class<?> loadClass(String name) throws ClassNotFoundException {
                return contextClassLoader.loadClass(name);
            }
        };
        metadataProvider = new MetadataProviderImpl(cdoUnit.getTypes(), datastore);
        try {
            this.validatorFactory = Validation.buildDefaultValidatorFactory();
        } catch (ValidationException e) {
            LOGGER.debug("Cannot find validation provider.", e);
            LOGGER.info("No JSR 303 Bean Validation provider available.");
        }
        datastore.init(metadataProvider);
    }

    @Override
    public CdoManager createCdoManager() {
        DatastoreSession datastoreSession = datastore.createSession(metadataProvider);
        CdoTransaction cdoTransaction = new CdoTransactionImpl(datastoreSession.getDatastoreTransaction());
        TransactionalCache cache = new TransactionalCache();
        InstanceValidator instanceValidator = new InstanceValidator(validatorFactory, cache);
        cdoTransaction.registerSynchronization(new ValidatorSynchronization(instanceValidator));
        cdoTransaction.registerSynchronization(new CacheSynchronization(cache));
        InterceptorFactory interceptorFactory = new InterceptorFactory(cdoTransaction, transactionAttribute);
        InstanceManager instanceManager = new InstanceManager(metadataProvider, datastoreSession, classLoader, cache, interceptorFactory);
        return new CdoManagerImpl(metadataProvider, cdoTransaction, datastoreSession, instanceManager, interceptorFactory, instanceValidator);
    }

    @Override
    public void close() {
        datastore.close();
    }

    public CdoUnit getCdoUnit() {
        return cdoUnit;
    }
}
