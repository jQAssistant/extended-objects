package com.buschmais.cdo.neo4j.impl;

import com.buschmais.cdo.api.bootstrap.CdoUnit;
import com.buschmais.cdo.neo4j.impl.datastore.EmbeddedNeo4jDatastore;
import com.buschmais.cdo.neo4j.impl.datastore.EmbeddedNeo4jDatastoreSession;
import com.buschmais.cdo.neo4j.impl.node.metadata.IndexedPropertyMethodMetadata;
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

    public EmbeddedNeo4jCdoManagerFactoryImpl(CdoUnit cdoUnit) {
        super(cdoUnit);
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
            IndexedPropertyMethodMetadata indexedPropertyMethodMetadata = nodeMetadata.getIndexedProperty();
            if (indexedPropertyMethodMetadata != null && indexedPropertyMethodMetadata.isCreate()) {
                Label label = nodeMetadata.getLabel();
                PrimitivePropertyMethodMetadata propertyMethodMetadata = indexedPropertyMethodMetadata.getPropertyMethodMetadata();
                if (label != null && propertyMethodMetadata != null) {
                    IndexDefinition index = null;
                    for (IndexDefinition indexDefinition : graphDatabaseService.schema().getIndexes(label)) {
                        for (String s : indexDefinition.getPropertyKeys()) {
                            if (s.equals(propertyMethodMetadata.getPropertyName())) {
                                index = indexDefinition;
                            }
                        }
                    }
                    if (propertyMethodMetadata != null && index == null) {
                        LOGGER.info("Creating index for label {} on property '{}'.", label, propertyMethodMetadata.getPropertyName());
                        graphDatabaseService.schema().indexFor(label).on(propertyMethodMetadata.getPropertyName()).create();
                    } else if (propertyMethodMetadata == null && index != null) {
                        LOGGER.info("Dropping index for label {} on properties '{}'.", label, index.getPropertyKeys());
                        index.drop();
                    }
                }
            }
        }
        transaction.success();
        transaction.close();
    }

}
