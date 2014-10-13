package com.buschmais.xo.neo4j.impl.datastore;

import com.buschmais.xo.neo4j.impl.datastore.metadata.IndexedPropertyMetadata;
import com.buschmais.xo.neo4j.impl.datastore.metadata.NodeMetadata;
import com.buschmais.xo.neo4j.impl.datastore.metadata.PropertyMetadata;
import com.buschmais.xo.spi.metadata.method.IndexedPropertyMethodMetadata;
import com.buschmais.xo.spi.metadata.method.PrimitivePropertyMethodMetadata;
import com.buschmais.xo.spi.metadata.type.EntityTypeMetadata;
import com.buschmais.xo.spi.metadata.type.TypeMetadata;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.schema.IndexDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * Abstract base implementation for embedded graph stores.
 */
public abstract class AbstractEmbeddedNeo4jDatastore extends AbstractNeo4jDatastore<EmbeddedNeo4jDatastoreSession> {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmbeddedNeo4jDatastore.class);
    protected final GraphDatabaseService graphDatabaseService;

    public AbstractEmbeddedNeo4jDatastore(GraphDatabaseService graphDatabaseService) {
        this.graphDatabaseService = graphDatabaseService;
    }

    @Override
    public EmbeddedNeo4jDatastoreSession createSession() {
        return new EmbeddedNeo4jDatastoreSession(graphDatabaseService);
    }

    @Override
    public void init(Collection<TypeMetadata> registeredMetadata) {
        try (Transaction transaction = graphDatabaseService.beginTx()) {
            for (TypeMetadata typeMetadata : registeredMetadata) {
                if (typeMetadata instanceof EntityTypeMetadata) {
                    EntityTypeMetadata<NodeMetadata> entityTypeMetadata = (EntityTypeMetadata<NodeMetadata>) typeMetadata;
                    IndexedPropertyMethodMetadata<IndexedPropertyMetadata> indexedPropertyMethodMetadata = entityTypeMetadata.getIndexedProperty();
                    if (indexedPropertyMethodMetadata != null) {
                        IndexedPropertyMetadata datastoreMetadata = indexedPropertyMethodMetadata.getDatastoreMetadata();
                        if (datastoreMetadata.isCreate()) {
                            initIndex(entityTypeMetadata, indexedPropertyMethodMetadata.getPropertyMethodMetadata(), datastoreMetadata.isUnique());
                        }
                    }
                }
            }
            transaction.success();
        }
    }

    private void initIndex(EntityTypeMetadata<NodeMetadata> entityTypeMetadata, PrimitivePropertyMethodMetadata propertyMethodMetadata, boolean unique) {
        Label label = entityTypeMetadata.getDatastoreMetadata().getDiscriminator();
        if (label != null && propertyMethodMetadata != null) {
            reCreateIndex(label, propertyMethodMetadata, unique);
        }
    }

    private void reCreateIndex(Label label, PrimitivePropertyMethodMetadata propertyMethodMetadata, boolean unique) {
        PropertyMetadata propertyMetadata = ((PrimitivePropertyMethodMetadata<PropertyMetadata>) propertyMethodMetadata).getDatastoreMetadata();
        IndexDefinition index = findIndex(label, propertyMetadata.getName());
        if (index == null) {
            if (unique) {
                LOGGER.info("Creating constraint for label {} on property '{}'.", label, propertyMetadata.getName());
                graphDatabaseService.schema().constraintFor(label).assertPropertyIsUnique(propertyMetadata.getName()).create();
            } else {
                LOGGER.info("Creating index for label {} on property '{}'.", label, propertyMetadata.getName());
                graphDatabaseService.schema().indexFor(label).on(propertyMetadata.getName()).create();
            }
        }
    }

    private IndexDefinition findIndex(Label label, String propertyName) {
        final Iterable<IndexDefinition> indexes = graphDatabaseService.schema().getIndexes(label);
        for (IndexDefinition indexDefinition : indexes) {
            for (String key : indexDefinition.getPropertyKeys()) {
                if (key.equals(propertyName)) {
                    return indexDefinition;
                }
            }
        }
        return null;
    }
}
