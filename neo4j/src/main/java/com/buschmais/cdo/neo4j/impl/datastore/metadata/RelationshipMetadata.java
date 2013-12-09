package com.buschmais.cdo.neo4j.impl.datastore.metadata;

import org.neo4j.graphdb.RelationshipType;

public class RelationshipMetadata {

    private RelationshipType relationshipType;

    public RelationshipMetadata(RelationshipType relationshipType) {
        this.relationshipType = relationshipType;
    }

    public RelationshipType getRelationshipType() {
        return relationshipType;
    }
}
