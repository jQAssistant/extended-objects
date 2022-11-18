package com.buschmais.xo.neo4j.embedded.impl.datastore;

import com.buschmais.xo.neo4j.embedded.api.EmbeddedNeo4jDatastoreSession;
import com.buschmais.xo.neo4j.embedded.impl.model.EmbeddedLabel;
import com.buschmais.xo.neo4j.embedded.impl.model.EmbeddedRelationshipType;
import com.buschmais.xo.neo4j.spi.AbstractNeo4jDatastore;
import com.buschmais.xo.neo4j.spi.AbstractNeo4jMetadataFactory;
import com.buschmais.xo.neo4j.spi.metadata.NodeMetadata;
import com.buschmais.xo.neo4j.spi.metadata.RelationshipMetadata;
import com.buschmais.xo.spi.datastore.DatastoreMetadataFactory;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.RelationshipType;

/**
 * Abstract base implementation for embedded graph stores.
 */
public abstract class AbstractEmbeddedDatastore extends AbstractNeo4jDatastore<EmbeddedLabel, EmbeddedRelationshipType, EmbeddedNeo4jDatastoreSession> {

    protected final GraphDatabaseService graphDatabaseService;

    private final EmbeddedDatastoreTransaction transaction;

    /**
     * Constructor.
     *
     * @param graphDatabaseService
     */
    public AbstractEmbeddedDatastore(GraphDatabaseService graphDatabaseService) {
        this.graphDatabaseService = graphDatabaseService;
        this.transaction = new EmbeddedDatastoreTransaction(graphDatabaseService);
    }

    @Override
    public DatastoreMetadataFactory<NodeMetadata<EmbeddedLabel>, EmbeddedLabel, RelationshipMetadata<EmbeddedRelationshipType>, EmbeddedRelationshipType> getMetadataFactory() {
        return new AbstractNeo4jMetadataFactory<>() {

            protected EmbeddedLabel createLabel(String value) {
                return new EmbeddedLabel(value);
            }

            protected EmbeddedRelationshipType createRelationshipType(String name) {
                return new EmbeddedRelationshipType(RelationshipType.withName(name));
            }

            @Override
            protected boolean isBatchableDefault() {
                return false;
            }
        };
    }

    @Override
    public EmbeddedNeo4jDatastoreSession createSession() {
        return new EmbeddedDatastoreSessionImpl(transaction, graphDatabaseService);
    }

}
