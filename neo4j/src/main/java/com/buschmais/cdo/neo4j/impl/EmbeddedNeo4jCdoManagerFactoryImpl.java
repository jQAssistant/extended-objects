package com.buschmais.cdo.neo4j.impl;

import com.buschmais.cdo.api.bootstrap.CdoUnit;
import com.buschmais.cdo.neo4j.impl.datastore.EmbeddedNeo4jDatastore;
import com.buschmais.cdo.neo4j.impl.datastore.EmbeddedNeo4jDatastoreSession;
import com.buschmais.cdo.neo4j.impl.datastore.metadata.PrimitivePropertyMetadata;
import com.buschmais.cdo.neo4j.impl.node.metadata.IndexedPropertyMethodMetadata;
import com.buschmais.cdo.neo4j.impl.node.metadata.EntityMetadata;
import com.buschmais.cdo.neo4j.impl.node.metadata.MetadataProvider;
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

    protected EmbeddedNeo4jDatastore createDatastore(URL url) {
        GraphDatabaseService graphDatabaseService = new GraphDatabaseFactory().newEmbeddedDatabase(url.getPath());
        return new EmbeddedNeo4jDatastore(graphDatabaseService);
    }

    @Override
    protected void init(EmbeddedNeo4jDatastore datastore, MetadataProvider metadataProvider) {
        EmbeddedNeo4jDatastoreSession session = datastore.createSession(metadataProvider);
        GraphDatabaseService graphDatabaseService = session.getGraphDatabaseService();
        try (Transaction transaction = graphDatabaseService.beginTx()) {
            for (EntityMetadata entityMetadata : metadataProvider.getRegisteredNodeMetadata()) {
                IndexedPropertyMethodMetadata indexedPropertyMethodMetadata = entityMetadata.getIndexedProperty();
                if (indexedPropertyMethodMetadata != null && indexedPropertyMethodMetadata.isCreate()) {
                    Label label = entityMetadata.getLabel();
                    PrimitivePropertyMethodMetadata propertyMethodMetadata = indexedPropertyMethodMetadata.getPropertyMethodMetadata();
                    if (label != null && propertyMethodMetadata != null) {
                        reCreateIndex(graphDatabaseService, label, propertyMethodMetadata);
                    }
                }
            }
            transaction.success();
        }
    }

    private void reCreateIndex(GraphDatabaseService graphDatabaseService, Label label, PrimitivePropertyMethodMetadata propertyMethodMetadata) {
        PrimitivePropertyMetadata primitivePropertyMetadata = ((PrimitivePropertyMethodMetadata<PrimitivePropertyMetadata>)propertyMethodMetadata).getDatastoreMetadata();
        IndexDefinition index = findIndex(graphDatabaseService, label, primitivePropertyMetadata);
        if (propertyMethodMetadata != null && index == null) {
            LOGGER.info("Creating index for label {} on property '{}'.", label, primitivePropertyMetadata.getName());
            graphDatabaseService.schema().indexFor(label).on(primitivePropertyMetadata.getName()).create();
        } else if (propertyMethodMetadata == null && index != null) {
            LOGGER.info("Dropping index for label {} on properties '{}'.", label, index.getPropertyKeys());
            index.drop();
        }
    }

    private IndexDefinition findIndex(GraphDatabaseService graphDatabaseService, Label label, PrimitivePropertyMetadata primitivePropertyMetadata) {
        for (IndexDefinition indexDefinition : graphDatabaseService.schema().getIndexes(label)) {
            for (String s : indexDefinition.getPropertyKeys()) {
                if (s.equals(primitivePropertyMetadata.getName())) {
                    return indexDefinition;
                }
            }
        }
        return null;
    }

}
