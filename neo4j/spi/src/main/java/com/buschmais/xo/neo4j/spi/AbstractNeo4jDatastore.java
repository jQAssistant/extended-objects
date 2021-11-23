package com.buschmais.xo.neo4j.spi;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.api.metadata.method.IndexedPropertyMethodMetadata;
import com.buschmais.xo.api.metadata.method.PrimitivePropertyMethodMetadata;
import com.buschmais.xo.api.metadata.type.EntityTypeMetadata;
import com.buschmais.xo.api.metadata.type.TypeMetadata;
import com.buschmais.xo.neo4j.api.model.Neo4jLabel;
import com.buschmais.xo.neo4j.api.model.Neo4jRelationshipType;
import com.buschmais.xo.neo4j.spi.Neo4jDatastoreSession.Index;
import com.buschmais.xo.neo4j.spi.metadata.IndexedPropertyMetadata;
import com.buschmais.xo.neo4j.spi.metadata.NodeMetadata;
import com.buschmais.xo.neo4j.spi.metadata.PropertyMetadata;
import com.buschmais.xo.spi.datastore.DatastoreTransaction;

public abstract class AbstractNeo4jDatastore<L extends Neo4jLabel, R extends Neo4jRelationshipType, DS extends Neo4jDatastoreSession>
        implements Neo4jDatastore<L, R, DS> {

    @Override
    public void init(Map<Class<?>, TypeMetadata> registeredMetadata) {
        Set<Index> indexes = new HashSet<>();
        for (TypeMetadata typeMetadata : registeredMetadata.values()) {
            if (typeMetadata instanceof EntityTypeMetadata) {
                EntityTypeMetadata<NodeMetadata<L>> entityTypeMetadata = (EntityTypeMetadata<NodeMetadata<L>>) typeMetadata;
                // check for indexed property declared in type
                Optional<Index> labelIndex = getRequiredIndex(entityTypeMetadata, entityTypeMetadata.getIndexedProperty());
                labelIndex.ifPresent(index -> indexes.add(index));
                // check for inherited indexed property
                getRequiredIndex(entityTypeMetadata, entityTypeMetadata.getDatastoreMetadata().getUsingIndexedPropertyOf())
                        .ifPresent(index -> indexes.add(index));
            }
        }
        try (DS session = createSession()) {
            Set<Index> existingIndexes = inTransaction(session, () -> session.getIndexes());
            indexes.removeAll(existingIndexes);
            inTransaction(session, () -> session.createIndexes(indexes));
        }
    }

    private void inTransaction(DS session, Runnable runnable) {
        inTransaction(session, () -> {
            runnable.run();
            return null;
        });
    }

    private <T> T inTransaction(DS session, Supplier<T> supplier) {
        DatastoreTransaction transaction = session.getDatastoreTransaction();
        transaction.begin();
        try {
            T t = supplier.get();
            transaction.commit();
            return t;
        } catch (XOException e) {
            transaction.rollback();
            throw e;
        }
    }

    /**
     * Determines if there's a required index defined for an entity.
     *
     * @param entityTypeMetadata
     *            The entity.
     * @param indexedProperty
     *            The index metadata.
     */
    private Optional<Index> getRequiredIndex(EntityTypeMetadata<NodeMetadata<L>> entityTypeMetadata,
            IndexedPropertyMethodMetadata<IndexedPropertyMetadata> indexedProperty) {
        if (indexedProperty != null) {
            L label = entityTypeMetadata.getDatastoreMetadata().getDiscriminator();
            PrimitivePropertyMethodMetadata<PropertyMetadata> propertyMethodMetadata = indexedProperty.getPropertyMethodMetadata();
            if (label != null && propertyMethodMetadata != null) {
                return Optional.of(Index.builder().label(label.getName()).property(propertyMethodMetadata.getDatastoreMetadata().getName()).build());
            }
        }
        return Optional.empty();
    }

}
