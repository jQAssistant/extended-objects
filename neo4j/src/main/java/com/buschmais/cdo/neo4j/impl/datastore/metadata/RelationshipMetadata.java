package com.buschmais.cdo.neo4j.impl.datastore.metadata;

import com.buschmais.cdo.spi.datastore.DatastoreRelationMetadata;
import org.neo4j.graphdb.RelationshipType;

public class RelationshipMetadata implements DatastoreRelationMetadata<RelationshipType> {

    private RelationshipType relationshipType;

    public RelationshipMetadata(RelationshipType relationshipType) {
        this.relationshipType = relationshipType;
    }

    @Override
    public RelationshipType getDiscriminator() {
        return relationshipType;
    }
}
