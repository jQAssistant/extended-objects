package com.buschmais.cdo.neo4j.impl.datastore.metadata;

import com.buschmais.cdo.spi.datastore.DatastoreRelationMetadata;

public class RelationshipMetadata implements DatastoreRelationMetadata<Neo4jRelationshipType> {

    private Neo4jRelationshipType relationshipType;

    public RelationshipMetadata(Neo4jRelationshipType relationshipType) {
        this.relationshipType = relationshipType;
    }

    @Override
    public Neo4jRelationshipType getDiscriminator() {
        return relationshipType;
    }
}
