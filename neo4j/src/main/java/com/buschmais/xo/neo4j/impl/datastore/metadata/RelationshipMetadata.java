package com.buschmais.xo.neo4j.impl.datastore.metadata;

import com.buschmais.xo.spi.datastore.DatastoreRelationMetadata;

public class RelationshipMetadata implements DatastoreRelationMetadata<RelationshipType> {

    private final RelationshipType relationshipType;

    public RelationshipMetadata(RelationshipType relationshipType) {
        this.relationshipType = relationshipType;
    }

    @Override
    public RelationshipType getDiscriminator() {
        return relationshipType;
    }
}
