package com.buschmais.xo.neo4j.spi.metadata;

import com.buschmais.xo.neo4j.api.model.Neo4jRelationshipType;
import com.buschmais.xo.spi.datastore.DatastoreRelationMetadata;

public class RelationshipMetadata<T extends Neo4jRelationshipType> implements DatastoreRelationMetadata<T> {

    private final T relationshipType;

    public RelationshipMetadata(T relationshipType) {
        this.relationshipType = relationshipType;
    }

    @Override
    public T getDiscriminator() {
        return relationshipType;
    }
}
