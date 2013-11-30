package com.buschmais.cdo.neo4j.impl;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.api.CdoManagerFactory;
import com.buschmais.cdo.api.bootstrap.CdoUnit;
import com.buschmais.cdo.neo4j.impl.cache.TransactionalCache;
import com.buschmais.cdo.neo4j.impl.node.InstanceManager;
import com.buschmais.cdo.neo4j.impl.node.metadata.NodeMetadataProvider;
import com.buschmais.cdo.neo4j.spi.Datastore;
import com.buschmais.cdo.neo4j.spi.DatastoreSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.ValidatorFactory;
import java.net.URL;
import java.util.Arrays;

public abstract class AbstractNeo4jCdoManagerFactoryImpl<D extends Datastore> implements CdoManagerFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractNeo4jCdoManagerFactoryImpl.class);

    private URL url;
    private NodeMetadataProvider nodeMetadataProvider;
    private ClassLoader classLoader;
    private D datastore;
    private ValidatorFactory validatorFactory;


    protected AbstractNeo4jCdoManagerFactoryImpl(CdoUnit cdoUnit) {
        this.url = cdoUnit.getUrl();
        nodeMetadataProvider = new NodeMetadataProvider(cdoUnit.getTypes());
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
        TransactionalCache cache = new TransactionalCache();
        DatastoreSession datastoreSession = datastore.createSession();
        InstanceManager instanceManager = new InstanceManager(nodeMetadataProvider, datastoreSession, classLoader, cache);
        return new Neo4jCdoManagerImpl(datastoreSession, instanceManager, cache, validatorFactory);
    }


    @Override
    public void close() {
        datastore.close();
    }

    protected abstract D createDatastore(URL url, NodeMetadataProvider metadataProvider);

    protected abstract void init(D datastore, NodeMetadataProvider nodeMetadataProvider);

}
