package com.buschmais.xo.neo4j.embedded.impl.datastore;

import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;

import com.buschmais.xo.neo4j.embedded.impl.model.EmbeddedLabel;
import com.buschmais.xo.neo4j.embedded.impl.model.EmbeddedRelationshipType;
import com.buschmais.xo.neo4j.spi.AbstractNeo4jDatastore;
import com.buschmais.xo.neo4j.spi.AbstractNeo4jMetadataFactory;
import com.buschmais.xo.neo4j.spi.metadata.NodeMetadata;
import com.buschmais.xo.neo4j.spi.metadata.RelationshipMetadata;
import com.buschmais.xo.spi.datastore.DatastoreMetadataFactory;

/**
 * Abstract base implementation for embedded graph stores.
 */
public abstract class AbstractEmbeddedNeo4jDatastore extends AbstractNeo4jDatastore<EmbeddedLabel, EmbeddedRelationshipType, EmbeddedNeo4jDatastoreSession> {

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
    public DatastoreMetadataFactory<NodeMetadata<EmbeddedLabel>, EmbeddedLabel, RelationshipMetadata<EmbeddedRelationshipType>, EmbeddedRelationshipType> getMetadataFactory() {
        return new AbstractNeo4jMetadataFactory<EmbeddedLabel, EmbeddedRelationshipType>() {

            protected EmbeddedLabel createLabel(String value) {
                return new EmbeddedLabel(value);
            }

            protected EmbeddedRelationshipType createRelationshipType(String name) {
                return new EmbeddedRelationshipType(DynamicRelationshipType.withName(name));
            }
        };
    }

    @Override
    public EmbeddedNeo4jDatastoreSession createSession() {
        return new EmbeddedNeo4jDatastoreSession(graphDatabaseService);
    }
}
