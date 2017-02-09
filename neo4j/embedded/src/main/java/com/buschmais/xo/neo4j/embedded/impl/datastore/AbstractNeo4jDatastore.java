package com.buschmais.xo.neo4j.embedded.impl.datastore;

import org.neo4j.graphdb.DynamicRelationshipType;

import com.buschmais.xo.neo4j.embedded.api.Neo4jDatastore;
import com.buschmais.xo.neo4j.embedded.api.Neo4jDatastoreSession;
import com.buschmais.xo.neo4j.embedded.impl.model.EmbeddedLabel;
import com.buschmais.xo.neo4j.embedded.impl.model.EmbeddedRelationshipType;
import com.buschmais.xo.neo4j.spi.AbstractNeo4jMetadataFactory;
import com.buschmais.xo.neo4j.spi.metadata.NodeMetadata;
import com.buschmais.xo.neo4j.spi.metadata.RelationshipMetadata;
import com.buschmais.xo.spi.datastore.DatastoreMetadataFactory;

/**
 * Abstract base class for Neo4j datastores.
 * 
 * @param <DS>
 *            The datastore session type.
 */
public abstract class AbstractNeo4jDatastore<DS extends Neo4jDatastoreSession> implements Neo4jDatastore<EmbeddedLabel, EmbeddedRelationshipType, DS> {

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

}
