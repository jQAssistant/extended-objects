package com.buschmais.cdo.neo4j.impl;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.api.CdoManagerFactory;
import com.buschmais.cdo.api.CdoTransaction;
import com.buschmais.cdo.api.bootstrap.CdoUnit;
import com.buschmais.cdo.impl.cache.TransactionalCache;
import com.buschmais.cdo.neo4j.impl.common.*;
import com.buschmais.cdo.neo4j.impl.common.InstanceManager;
import com.buschmais.cdo.neo4j.impl.node.metadata.NodeMetadataProvider;
import com.buschmais.cdo.neo4j.spi.Datastore;
import com.buschmais.cdo.neo4j.spi.DatastoreSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.ValidatorFactory;
import java.net.URL;

public abstract class AbstractNeo4jCdoManagerFactoryImpl<D extends Datastore> implements CdoManagerFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractNeo4jCdoManagerFactoryImpl.class);

    private URL url;
    private NodeMetadataProvider nodeMetadataProvider;
    private ClassLoader classLoader;
    private D datastore;
    private ValidatorFactory validatorFactory;
    private CdoUnit.TransactionAttribute transactionAttribute;

    protected AbstractNeo4jCdoManagerFactoryImpl(CdoUnit cdoUnit) {
        this.url = cdoUnit.getUrl();
        nodeMetadataProvider = new NodeMetadataProvider(cdoUnit.getTypes());
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
        this.datastore = createDatastore(url, nodeMetadataProvider);
        try {
            this.validatorFactory = Validation.buildDefaultValidatorFactory();
        } catch (ValidationException e) {
            LOGGER.debug("Cannot find validation provider.", e);
            LOGGER.info("No JSR 303 Bean Validation provider available.");
        }
        this.init(datastore, nodeMetadataProvider);
    }

    @Override
    public CdoManager createCdoManager() {
        DatastoreSession datastoreSession = datastore.createSession();
        CdoTransaction cdoTransaction = new CdoTransactionImpl(datastoreSession.getDatastoreTransaction());
        TransactionalCache cache = new TransactionalCache();
        InstanceValidator instanceValidator = new InstanceValidator(validatorFactory, cache);
        cdoTransaction.registerSynchronization(new ValidatorSynchronization(instanceValidator));
        cdoTransaction.registerSynchronization(new CacheSynchronization(cache));
        InstanceManager instanceManager = new InstanceManager(cdoTransaction, nodeMetadataProvider, datastoreSession, classLoader, cache, transactionAttribute);
        return new CdoManagerImpl(cdoTransaction, datastoreSession, instanceManager, instanceValidator);
    }


    @Override
    public void close() {
        datastore.close();
    }

    protected abstract D createDatastore(URL url, NodeMetadataProvider metadataProvider);

    protected abstract void init(D datastore, NodeMetadataProvider nodeMetadataProvider);

}
