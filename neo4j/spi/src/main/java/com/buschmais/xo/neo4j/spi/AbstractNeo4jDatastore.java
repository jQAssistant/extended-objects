package com.buschmais.xo.neo4j.spi;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.neo4j.api.model.Neo4jLabel;
import com.buschmais.xo.neo4j.api.model.Neo4jRelationshipType;
import com.buschmais.xo.neo4j.spi.Neo4jDatastoreSession.Index;
import com.buschmais.xo.neo4j.spi.metadata.IndexedPropertyMetadata;
import com.buschmais.xo.neo4j.spi.metadata.NodeMetadata;
import com.buschmais.xo.neo4j.spi.metadata.PropertyMetadata;
import com.buschmais.xo.spi.datastore.DatastoreTransaction;
import com.buschmais.xo.spi.metadata.method.IndexedPropertyMethodMetadata;
import com.buschmais.xo.spi.metadata.method.PrimitivePropertyMethodMetadata;
import com.buschmais.xo.spi.metadata.type.EntityTypeMetadata;
import com.buschmais.xo.spi.metadata.type.TypeMetadata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractNeo4jDatastore<L extends Neo4jLabel, R extends Neo4jRelationshipType, DS extends Neo4jDatastoreSession>
        implements Neo4jDatastore<L, R, DS> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractNeo4jDatastore.class);

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
            DatastoreTransaction transaction = session.getDatastoreTransaction();
            transaction.begin();
            try {
                Set<Index> existingIndexes = session.getIndexes();
                indexes.removeAll(existingIndexes);
                session.createIndexes(indexes);
                transaction.commit();
            } catch (XOException e) {
                transaction.rollback();
                throw e;
            }
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
