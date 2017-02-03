package com.buschmais.xo.neo4j.impl.datastore;

import java.util.Map;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.schema.IndexDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.buschmais.xo.neo4j.impl.datastore.metadata.IndexedPropertyMetadata;
import com.buschmais.xo.neo4j.impl.datastore.metadata.NodeMetadata;
import com.buschmais.xo.neo4j.impl.datastore.metadata.PropertyMetadata;
import com.buschmais.xo.neo4j.impl.model.EmbeddedLabel;
import com.buschmais.xo.spi.metadata.method.IndexedPropertyMethodMetadata;
import com.buschmais.xo.spi.metadata.method.PrimitivePropertyMethodMetadata;
import com.buschmais.xo.spi.metadata.type.EntityTypeMetadata;
import com.buschmais.xo.spi.metadata.type.TypeMetadata;

/**
 * Abstract base implementation for embedded graph stores.
 */
public abstract class AbstractEmbeddedNeo4jDatastore extends AbstractNeo4jDatastore<EmbeddedNeo4jDatastoreSession> {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmbeddedNeo4jDatastore.class);
    protected final GraphDatabaseService graphDatabaseService;

    /**
     * Constructor.
     * 
     * @param graphDatabaseService
     *            The graph database service.
     */
    public AbstractEmbeddedNeo4jDatastore(GraphDatabaseService graphDatabaseService) {
        this.graphDatabaseService = graphDatabaseService;
    }

    @Override
    public EmbeddedNeo4jDatastoreSession createSession() {
        return new EmbeddedNeo4jDatastoreSession(graphDatabaseService);
    }

    @Override
    public void init(Map<Class<?>, TypeMetadata> registeredMetadata) {
        try (Transaction transaction = graphDatabaseService.beginTx()) {
            for (TypeMetadata typeMetadata : registeredMetadata.values()) {
                if (typeMetadata instanceof EntityTypeMetadata) {
                    EntityTypeMetadata<NodeMetadata> entityTypeMetadata = (EntityTypeMetadata<NodeMetadata>) typeMetadata;
                    // check for indexed property declared in type
                    ensureIndex(entityTypeMetadata, entityTypeMetadata.getIndexedProperty());
                    ensureIndex(entityTypeMetadata, entityTypeMetadata.getDatastoreMetadata().getUsingIndexedPropertyOf());
                }
            }
            transaction.success();
        }
    }

    /**
     * Ensures that an index exists for the given entity and property.
     * 
     * @param entityTypeMetadata
     *            The entity.
     * @param indexedProperty
     *            The index metadata.
     */
    private void ensureIndex(EntityTypeMetadata<NodeMetadata> entityTypeMetadata, IndexedPropertyMethodMetadata<IndexedPropertyMetadata> indexedProperty) {
        if (indexedProperty != null) {
            IndexedPropertyMetadata datastoreMetadata = indexedProperty.getDatastoreMetadata();
            if (datastoreMetadata.isCreate()) {
                EmbeddedLabel label = entityTypeMetadata.getDatastoreMetadata().getDiscriminator();
                PrimitivePropertyMethodMetadata<PropertyMetadata> propertyMethodMetadata = indexedProperty.getPropertyMethodMetadata();
                if (label != null && propertyMethodMetadata != null) {
                    ensureIndex(label, propertyMethodMetadata, datastoreMetadata.isUnique());
                }
            }
        }
    }

    /**
     * Ensures that an index exists for the given label and property.
     *
     * @param label
     *            The label.
     * @param propertyMethodMetadata
     *            The property metadata.
     */
    private void ensureIndex(EmbeddedLabel label, PrimitivePropertyMethodMetadata<PropertyMetadata> propertyMethodMetadata, boolean unique) {
        PropertyMetadata propertyMetadata = propertyMethodMetadata.getDatastoreMetadata();
        IndexDefinition index = findIndex(label.getDelegate(), propertyMetadata.getName());
        if (index == null) {
            if (unique) {
                LOGGER.debug("Creating constraint for label {} on property '{}'.", label, propertyMetadata.getName());
                graphDatabaseService.schema().constraintFor(label.getDelegate()).assertPropertyIsUnique(propertyMetadata.getName()).create();
            } else {
                LOGGER.debug("Creating index for label {} on property '{}'.", label, propertyMetadata.getName());
                graphDatabaseService.schema().indexFor(label.getDelegate()).on(propertyMetadata.getName()).create();
            }
        }
    }

    /**
     * Find an existing index.
     * 
     * @param label
     *            The label.
     * @param propertyName
     *            The property name.
     * @return The index or <code>null</code> if it does not exist.
     */
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
