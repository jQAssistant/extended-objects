package com.buschmais.cdo.neo4j.impl;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.api.CdoManagerFactory;
import com.buschmais.cdo.neo4j.impl.metadata.NodeMetadata;
import com.buschmais.cdo.neo4j.impl.metadata.NodeMetadataProvider;
import com.buschmais.cdo.neo4j.impl.metadata.PrimitivePropertyMethodMetadata;
import com.buschmais.cdo.neo4j.impl.proxy.InstanceManager;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.schema.IndexDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Arrays;

public abstract class AbstractNeo4jCdoManagerFactoryImpl implements CdoManagerFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractNeo4jCdoManagerFactoryImpl.class);

    private URL url;
    private NodeMetadataProvider nodeMetadataProvider;
    private ClassLoader classLoader;

    public AbstractNeo4jCdoManagerFactoryImpl(URL url, Class<?>... entities) {
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
    }

    @Override
    public CdoManager createCdoManager() {
        this.updateIndexes();
        GraphDatabaseService graphDatabaseService = getGraphDatabaseService(url);
        InstanceManager instanceManager = new InstanceManager(nodeMetadataProvider, classLoader);
        return new EmbeddedNeo4jCdoManagerImpl(nodeMetadataProvider, graphDatabaseService, instanceManager);
    }

    private void updateIndexes() {
        GraphDatabaseService graphDatabaseService = getGraphDatabaseService(url);
        Transaction transaction = graphDatabaseService.beginTx();
        for (NodeMetadata nodeMetadata : nodeMetadataProvider.getRegisteredNodeMetadata()) {
            Label label = nodeMetadata.getLabel();
            PrimitivePropertyMethodMetadata indexedProperty = nodeMetadata.getIndexedProperty();
            if (label != null && indexedProperty != null) {
                IndexDefinition index = null;
                for (IndexDefinition indexDefinition : graphDatabaseService.schema().getIndexes(label)) {
                    for (String s : indexDefinition.getPropertyKeys()) {
                        if (s.equals(indexedProperty.getPropertyName())) {
                            index = indexDefinition;
                        }
                    }
                }
                if (indexedProperty != null && index == null) {
                    LOGGER.info("Creating index for label {} on property '{}'.", label, indexedProperty.getPropertyName());
                    graphDatabaseService.schema().indexFor(label).on(indexedProperty.getPropertyName()).create();
                } else if (indexedProperty == null && index != null) {
                    LOGGER.info("Dropping index for label {} on properties '{}'.", label, index.getPropertyKeys());
                    index.drop();
                }
            }
        }
        transaction.success();
        transaction.close();
    }

    protected abstract GraphDatabaseService getGraphDatabaseService(URL url);
}
