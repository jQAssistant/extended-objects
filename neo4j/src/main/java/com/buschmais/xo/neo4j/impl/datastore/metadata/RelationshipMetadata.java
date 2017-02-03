package com.buschmais.xo.neo4j.impl.datastore.metadata;

import com.buschmais.xo.neo4j.impl.model.EmbeddedRelationshipType;
import com.buschmais.xo.spi.datastore.DatastoreRelationMetadata;

public class RelationshipMetadata implements DatastoreRelationMetadata<EmbeddedRelationshipType> {

    private final EmbeddedRelationshipType relationshipType;

    public RelationshipMetadata(EmbeddedRelationshipType relationshipType) {
        this.relationshipType = relationshipType;
    }

    @Override
    public EmbeddedRelationshipType getDiscriminator() {
        return relationshipType;
    }
}
