package com.buschmais.cdo.neo4j.impl.datastore;

import com.buschmais.cdo.neo4j.impl.datastore.metadata.IndexedPropertyMetadata;
import com.buschmais.cdo.neo4j.impl.datastore.metadata.NodeMetadata;
import com.buschmais.cdo.neo4j.impl.datastore.metadata.PrimitivePropertyMetadata;
import com.buschmais.cdo.spi.metadata.method.IndexedPropertyMethodMetadata;
import com.buschmais.cdo.spi.metadata.method.PrimitivePropertyMethodMetadata;
import com.buschmais.cdo.spi.metadata.type.EntityTypeMetadata;
import com.buschmais.cdo.spi.metadata.type.TypeMetadata;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.schema.ConstraintDefinition;
import org.neo4j.graphdb.schema.ConstraintType;
import org.neo4j.graphdb.schema.IndexDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class EmbeddedNeo4jDatastore extends AbstractNeo4jDatastore<EmbeddedNeo4jDatastoreSession> {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmbeddedNeo4jDatastore.class);

    private final GraphDatabaseService graphDatabaseService;

    public EmbeddedNeo4jDatastore(GraphDatabaseService graphDatabaseService) {
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
                        if (indexedPropertyMethodMetadata.getDatastoreMetadata().isCreate()) {
                            initCreateIndex(entityTypeMetadata, indexedPropertyMethodMetadata.getPropertyMethodMetadata());
                        }

                        if (indexedPropertyMethodMetadata.getDatastoreMetadata().isUnique()) {
                            initUniqueIndex(entityTypeMetadata, indexedPropertyMethodMetadata.getPropertyMethodMetadata());
                        }
                    }
                }
            }
            transaction.success();
        }
    }

    private void initUniqueIndex(EntityTypeMetadata<NodeMetadata> entityTypeMetadata, PrimitivePropertyMethodMetadata propertyMethodMetadata) {
        Label label = entityTypeMetadata.getDatastoreMetadata().getDiscriminator();
        if (label != null && propertyMethodMetadata != null) {
            reCreateUniqueConstraint(label, propertyMethodMetadata);
        }
    }

    private void initCreateIndex(EntityTypeMetadata<NodeMetadata> entityTypeMetadata, PrimitivePropertyMethodMetadata propertyMethodMetadata) {
        Label label = entityTypeMetadata.getDatastoreMetadata().getDiscriminator();
        if (label != null && propertyMethodMetadata != null) {
            reCreateIndex(label, propertyMethodMetadata);
        }
    }

    private void reCreateIndex(Label label, PrimitivePropertyMethodMetadata propertyMethodMetadata) {
        PrimitivePropertyMetadata primitivePropertyMetadata = ((PrimitivePropertyMethodMetadata<PrimitivePropertyMetadata>) propertyMethodMetadata).getDatastoreMetadata();
        IndexDefinition index = findIndex(label, primitivePropertyMetadata.getName());
        //TODO propertyMethodMetadata is always != null
        if (propertyMethodMetadata != null && index == null) {
            LOGGER.info("Creating index for label {} on property '{}'.", label, primitivePropertyMetadata.getName());
            graphDatabaseService.schema().indexFor(label).on(primitivePropertyMetadata.getName()).create();
        } else if (propertyMethodMetadata == null && index != null) {
            LOGGER.info("Dropping index for label {} on properties '{}'.", label, index.getPropertyKeys());
            index.drop();
        }
    }


    private void reCreateUniqueConstraint(Label label, PrimitivePropertyMethodMetadata propertyMethodMetadata) {
        PrimitivePropertyMetadata primitivePropertyMetadata = ((PrimitivePropertyMethodMetadata<PrimitivePropertyMetadata>) propertyMethodMetadata).getDatastoreMetadata();
        ConstraintDefinition contraint = findUniqueConstraint(label, primitivePropertyMetadata.getName());
        //TODO propertyMethodMetadata is always != null
        if (propertyMethodMetadata != null && contraint == null) {
            LOGGER.info("Creating contraint for label {} on property '{}'.", label, primitivePropertyMetadata.getName());
            graphDatabaseService.schema().constraintFor(label).assertPropertyIsUnique(primitivePropertyMetadata.getName()).create();
        } else if (propertyMethodMetadata == null && contraint != null) {
            LOGGER.info("Dropping constraint for label {} on properties '{}'.", label, contraint.getPropertyKeys());
            contraint.drop();
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

    private ConstraintDefinition findUniqueConstraint(Label label, String propertyName) {
        final Iterable<ConstraintDefinition> constraints = graphDatabaseService.schema().getConstraints(label);
        for (ConstraintDefinition constraintDefinition : constraints) {
            if (constraintDefinition.isConstraintType(ConstraintType.UNIQUENESS)) {
                for (String key : constraintDefinition.getPropertyKeys()) {
                    if (key.equals(propertyName)) {
                        return constraintDefinition;
                    }
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
