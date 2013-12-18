package com.buschmais.cdo.neo4j.impl.datastore;

import com.buschmais.cdo.neo4j.impl.datastore.metadata.IndexedPropertyMetadata;
import com.buschmais.cdo.neo4j.impl.datastore.metadata.NodeMetadata;
import com.buschmais.cdo.neo4j.impl.datastore.metadata.PrimitivePropertyMetadata;
import com.buschmais.cdo.spi.metadata.IndexedPropertyMethodMetadata;
import com.buschmais.cdo.spi.metadata.PrimitivePropertyMethodMetadata;
import com.buschmais.cdo.spi.metadata.TypeMetadata;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.schema.IndexDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class EmbeddedNeo4jDatastore extends AbstractNeo4jDatastore<EmbeddedNeo4jDatastoreSession> {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmbeddedNeo4jDatastore.class);

    private GraphDatabaseService graphDatabaseService;

    public EmbeddedNeo4jDatastore(GraphDatabaseService graphDatabaseService) {
        this.graphDatabaseService = graphDatabaseService;
    }

    @Override
    public EmbeddedNeo4jDatastoreSession createSession() {
        return new EmbeddedNeo4jDatastoreSession(graphDatabaseService);
    }

    @Override
    public void init(Collection<TypeMetadata<NodeMetadata>> registeredMetadata) {
        EmbeddedNeo4jDatastoreSession session = createSession();
        GraphDatabaseService graphDatabaseService = session.getGraphDatabaseService();
        try (Transaction transaction = graphDatabaseService.beginTx()) {
            for (TypeMetadata<NodeMetadata> typeMetadata : registeredMetadata) {
                IndexedPropertyMethodMetadata<IndexedPropertyMetadata> indexedPropertyMethodMetadata = typeMetadata.getIndexedProperty();
                if (indexedPropertyMethodMetadata != null && indexedPropertyMethodMetadata.getDatastoreMetadata().isCreate()) {
                    Label label = typeMetadata.getDatastoreMetadata().getDiscriminator();
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
        PrimitivePropertyMetadata primitivePropertyMetadata = ((PrimitivePropertyMethodMetadata<PrimitivePropertyMetadata>) propertyMethodMetadata).getDatastoreMetadata();
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

    @Override
    public void close() {
        graphDatabaseService.shutdown();
    }
}
