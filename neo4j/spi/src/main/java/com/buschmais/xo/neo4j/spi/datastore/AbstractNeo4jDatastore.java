package com.buschmais.xo.neo4j.spi.datastore;

import java.util.Collections;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.buschmais.xo.api.ResultIterator;
import com.buschmais.xo.neo4j.api.annotation.Cypher;
import com.buschmais.xo.neo4j.api.model.Neo4jLabel;
import com.buschmais.xo.neo4j.api.model.Neo4jRelationshipType;
import com.buschmais.xo.neo4j.spi.metadata.IndexedPropertyMetadata;
import com.buschmais.xo.neo4j.spi.metadata.NodeMetadata;
import com.buschmais.xo.neo4j.spi.metadata.PropertyMetadata;
import com.buschmais.xo.neo4j.spi.metadata.RelationshipMetadata;
import com.buschmais.xo.spi.datastore.Datastore;
import com.buschmais.xo.spi.datastore.DatastoreSession;
import com.buschmais.xo.spi.datastore.DatastoreTransaction;
import com.buschmais.xo.spi.metadata.method.IndexedPropertyMethodMetadata;
import com.buschmais.xo.spi.metadata.method.PrimitivePropertyMethodMetadata;
import com.buschmais.xo.spi.metadata.type.EntityTypeMetadata;
import com.buschmais.xo.spi.metadata.type.TypeMetadata;

public abstract class AbstractNeo4jDatastore<L extends Neo4jLabel, R extends Neo4jRelationshipType, DS extends DatastoreSession>
        implements Datastore<DS, NodeMetadata<L>, L, RelationshipMetadata<R>, R> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractNeo4jDatastore.class);

    @Override
    public void init(Map<Class<?>, TypeMetadata> registeredMetadata) {
        try (DS session = createSession()) {
            DatastoreTransaction transaction = session.getDatastoreTransaction();
            transaction.begin();
            try {
                for (TypeMetadata typeMetadata : registeredMetadata.values()) {
                    if (typeMetadata instanceof EntityTypeMetadata) {
                        EntityTypeMetadata<NodeMetadata<L>> entityTypeMetadata = (EntityTypeMetadata<NodeMetadata<L>>) typeMetadata;
                        // check for indexed property declared in type
                        ensureIndex(session, entityTypeMetadata, entityTypeMetadata.getIndexedProperty());
                        ensureIndex(session, entityTypeMetadata, entityTypeMetadata.getDatastoreMetadata().getUsingIndexedPropertyOf());
                    }
                }
            } finally {
                transaction.commit();
            }
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
    private void ensureIndex(DS session, EntityTypeMetadata<NodeMetadata<L>> entityTypeMetadata,
            IndexedPropertyMethodMetadata<IndexedPropertyMetadata> indexedProperty) {
        if (indexedProperty != null) {
            IndexedPropertyMetadata datastoreMetadata = indexedProperty.getDatastoreMetadata();
            if (datastoreMetadata.isCreate()) {
                L label = entityTypeMetadata.getDatastoreMetadata().getDiscriminator();
                PrimitivePropertyMethodMetadata<PropertyMetadata> propertyMethodMetadata = indexedProperty.getPropertyMethodMetadata();
                if (label != null && propertyMethodMetadata != null) {
                    ensureIndex(session, label, propertyMethodMetadata, datastoreMetadata.isUnique());
                }
            }
        }
    }

    /**
     * Ensures that an index exists for the given label and property.
     *
     * @param session
     *            The datastore session
     * @param label
     *            The label.
     * @param propertyMethodMetadata
     *            The property metadata.
     * @param unique
     *            if <code>true</code> create a unique constraint
     */
    private void ensureIndex(DS session, L label, PrimitivePropertyMethodMetadata<PropertyMetadata> propertyMethodMetadata, boolean unique) {
        PropertyMetadata propertyMetadata = propertyMethodMetadata.getDatastoreMetadata();
        String statement;
        if (unique) {
            LOGGER.debug("Creating constraint for label {} on property '{}'.", label, propertyMetadata.getName());
            statement = String.format("CREATE CONSTRAINT ON (n:%s) ASSERT n.%s IS UNIQUE", label.getName(), propertyMetadata.getName());
        } else {
            LOGGER.debug("Creating index for label {} on property '{}'.", label, propertyMetadata.getName());
            statement = String.format("CREATE INDEX ON :%s(%s)", label.getName(), propertyMetadata.getName());
        }
        try (ResultIterator iterator = session.createQuery(Cypher.class).execute(statement, Collections.emptyMap())) {
        }
    }

}
