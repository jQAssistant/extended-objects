package com.buschmais.cdo.neo4j.impl;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.api.CdoManagerFactory;
import com.buschmais.cdo.neo4j.impl.cache.TransactionalCache;
import com.buschmais.cdo.neo4j.impl.node.InstanceManager;
import com.buschmais.cdo.neo4j.impl.node.metadata.NodeMetadataProvider;
import com.buschmais.cdo.neo4j.impl.query.QueryExecutor;
import org.neo4j.graphdb.GraphDatabaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.ValidatorFactory;
import java.net.URL;
import java.util.Arrays;

public abstract class AbstractNeo4jCdoManagerFactoryImpl<GDS extends GraphDatabaseService> implements CdoManagerFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractNeo4jCdoManagerFactoryImpl.class);

    private URL url;
    private NodeMetadataProvider nodeMetadataProvider;
    private ClassLoader classLoader;
    private GDS graphDatabaseService;
    private QueryExecutor queryExecutor;
    private ValidatorFactory validatorFactory;


    protected AbstractNeo4jCdoManagerFactoryImpl(URL url, Class<?>... entities) {
        this.url = url;
        nodeMetadataProvider = new NodeMetadataProvider(Arrays.asList(entities));
        final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        classLoader = contextClassLoader != null ? contextClassLoader : entities.getClass().getClassLoader();
        LOGGER.info("Using class loader '{}'.", contextClassLoader.toString());
        classLoader = new ClassLoader() {
            @Override
            public Class<?> loadClass(String name) throws ClassNotFoundException {
                return contextClassLoader.loadClass(name);
            }
        };
        this.graphDatabaseService = createGraphDatabaseService(url);
        this.queryExecutor = createQueryExecutor(this.graphDatabaseService);
        try {
            this.validatorFactory = Validation.buildDefaultValidatorFactory();
        } catch (ValidationException e) {
            LOGGER.debug("Cannot find validation provider.", e);
            LOGGER.info("No JSR 303 Bean Validation provider available.");
        }
        this.init(graphDatabaseService, nodeMetadataProvider);
    }

    @Override
    public CdoManager createCdoManager() {
        TransactionalCache cache = new TransactionalCache();
        InstanceManager instanceManager = new InstanceManager(nodeMetadataProvider, queryExecutor, classLoader, cache);
        return new Neo4jCdoManagerImpl(nodeMetadataProvider, graphDatabaseService, queryExecutor, instanceManager, cache, validatorFactory);
    }



    @Override
    public void close() {
        graphDatabaseService.shutdown();
    }

    protected abstract GDS createGraphDatabaseService(URL url);

    protected abstract QueryExecutor createQueryExecutor(GDS graphDatabaseService);

    protected abstract void init(GraphDatabaseService graphDatabaseService, NodeMetadataProvider nodeMetadataProvider);

}
