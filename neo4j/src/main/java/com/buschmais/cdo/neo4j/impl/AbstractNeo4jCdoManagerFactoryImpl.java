package com.buschmais.cdo.neo4j.impl;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.api.CdoManagerFactory;
import com.buschmais.cdo.neo4j.impl.metadata.NodeMetadata;
import com.buschmais.cdo.neo4j.impl.metadata.NodeMetadataProvider;
import com.buschmais.cdo.neo4j.impl.metadata.PrimitivePropertyMetadata;
import com.buschmais.cdo.neo4j.impl.proxy.InstanceManager;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.schema.IndexDefinition;

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
            PrimitivePropertyMetadata indexedProperty = nodeMetadata.getIndexedProperty();
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
                    graphDatabaseService.schema().indexFor(label).on(indexedProperty.getPropertyName()).create();
                } else if (indexedProperty == null && index != null) {
                    index.drop();
                }
            }
        }
        transaction.success();
        transaction.close();
    }

    protected abstract GraphDatabaseService getGraphDatabaseService(URL url);
}
