package com.buschmais.xo.neo4j.impl.datastore.metadata;

import com.buschmais.xo.spi.datastore.DatastoreRelationMetadata;

public class RelationshipMetadata implements DatastoreRelationMetadata<Neo4jRelationshipType> {

    private final Neo4jRelationshipType relationshipType;

    public RelationshipMetadata(Neo4jRelationshipType relationshipType) {
        this.relationshipType = relationshipType;
    }

    @Override
    public Neo4jRelationshipType getDiscriminator() {
        return relationshipType;
    }
}
