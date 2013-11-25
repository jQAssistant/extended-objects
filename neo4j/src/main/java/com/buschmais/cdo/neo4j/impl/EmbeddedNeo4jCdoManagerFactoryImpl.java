package com.buschmais.cdo.neo4j.impl;

import com.buschmais.cdo.neo4j.impl.datastore.EmbeddedNeo4jDatastore;
import com.buschmais.cdo.neo4j.impl.datastore.EmbeddedNeo4jDatastoreSession;
import com.buschmais.cdo.neo4j.impl.node.metadata.NodeMetadata;
import com.buschmais.cdo.neo4j.impl.node.metadata.NodeMetadataProvider;
import com.buschmais.cdo.neo4j.impl.node.metadata.PrimitivePropertyMethodMetadata;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.schema.IndexDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

public class EmbeddedNeo4jCdoManagerFactoryImpl extends AbstractNeo4jCdoManagerFactoryImpl<EmbeddedNeo4jDatastore> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractNeo4jCdoManagerFactoryImpl.class);

    public EmbeddedNeo4jCdoManagerFactoryImpl(URL url, Class<?>... entities) {
        super(url, entities);
    }

    protected EmbeddedNeo4jDatastore createDatastore(URL url, NodeMetadataProvider metadataProvider) {
        GraphDatabaseService graphDatabaseService = new GraphDatabaseFactory().newEmbeddedDatabase(url.getPath());
        return new EmbeddedNeo4jDatastore(graphDatabaseService, metadataProvider);
    }

    @Override
    protected void init(EmbeddedNeo4jDatastore datastore, NodeMetadataProvider nodeMetadataProvider) {
        EmbeddedNeo4jDatastoreSession session = datastore.createSession();
        GraphDatabaseService graphDatabaseService = session.getGraphDatabaseService();
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

}
