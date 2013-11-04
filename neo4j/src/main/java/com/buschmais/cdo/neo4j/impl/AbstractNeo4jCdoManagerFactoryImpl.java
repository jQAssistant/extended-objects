package com.buschmais.cdo.neo4j.impl;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.api.CdoManagerFactory;
import com.buschmais.cdo.neo4j.impl.metadata.NodeMetadataProvider;
import com.buschmais.cdo.neo4j.impl.proxy.InstanceManager;
import org.neo4j.graphdb.GraphDatabaseService;

import java.net.URL;
import java.util.Arrays;

public abstract class AbstractNeo4jCdoManagerFactoryImpl implements CdoManagerFactory {

    private URL url;
    private NodeMetadataProvider nodeMetadataProvider;
    private ClassLoader classLoader;

    public AbstractNeo4jCdoManagerFactoryImpl(URL url, Class<?>... entities) {
        this.url = url;
        nodeMetadataProvider = new NodeMetadataProvider(Arrays.asList(entities));
        classLoader = new ClassLoader() {
            @Override
            public Class<?> loadClass(String name) throws ClassNotFoundException {
                return super.loadClass(name);
            }
        };

    }

    @Override
    public CdoManager createCdoManager() {
        GraphDatabaseService graphDatabaseService = getGraphDatabaseService(url);
        InstanceManager instanceManager = new InstanceManager(nodeMetadataProvider, classLoader);
        return new CdoManagerImpl(nodeMetadataProvider, graphDatabaseService, instanceManager);
    }

    protected abstract GraphDatabaseService getGraphDatabaseService(URL url);
}
