package com.buschmais.cdo.impl;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.api.CdoManagerFactory;
import com.buschmais.cdo.api.CdoTransaction;
import com.buschmais.cdo.api.bootstrap.CdoUnit;
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
import java.net.URL;

public abstract class AbstractCdoManagerFactoryImpl<D extends Datastore> implements CdoManagerFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCdoManagerFactoryImpl.class);

    private URL url;
    private MetadataProvider metadataProvider;
    private ClassLoader classLoader;
    private D datastore;
    private ValidatorFactory validatorFactory;
    private CdoUnit.TransactionAttribute transactionAttribute;

    protected AbstractCdoManagerFactoryImpl(CdoUnit cdoUnit) {
        this.url = cdoUnit.getUrl();
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
        this.datastore = createDatastore(url);
        metadataProvider = new MetadataProviderImpl(cdoUnit.getTypes(), datastore);
        try {
            this.validatorFactory = Validation.buildDefaultValidatorFactory();
        } catch (ValidationException e) {
            LOGGER.debug("Cannot find validation provider.", e);
            LOGGER.info("No JSR 303 Bean Validation provider available.");
        }
        this.init(datastore, metadataProvider);
    }

    @Override
    public CdoManager createCdoManager() {
        DatastoreSession datastoreSession = datastore.createSession(metadataProvider);
        CdoTransaction cdoTransaction = new CdoTransactionImpl(datastoreSession.getDatastoreTransaction());
        TransactionalCache cache = new TransactionalCache();
        InstanceValidator instanceValidator = new InstanceValidator(validatorFactory, cache);
        cdoTransaction.registerSynchronization(new ValidatorSynchronization(instanceValidator));
        cdoTransaction.registerSynchronization(new CacheSynchronization(cache));
        InstanceManager instanceManager = new InstanceManager(cdoTransaction, metadataProvider, datastoreSession, classLoader, cache, transactionAttribute);
        return new CdoManagerImpl(metadataProvider, cdoTransaction, datastoreSession, instanceManager, instanceValidator);
    }


    @Override
    public void close() {
        datastore.close();
    }

    protected abstract D createDatastore(URL url);

    protected abstract void init(D datastore, MetadataProvider metadataProvider);

}
