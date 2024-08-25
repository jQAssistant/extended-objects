package com.buschmais.xo.neo4j.spi.metadata;

import com.buschmais.xo.api.metadata.type.DatastoreRelationMetadata;
import com.buschmais.xo.neo4j.api.model.Neo4jRelationshipType;

public class RelationshipMetadata<T extends Neo4jRelationshipType> extends AbstractPropertyContainerMetadata implements DatastoreRelationMetadata<T> {

    private final T relationshipType;

    public RelationshipMetadata(T relationshipType, boolean batchable) {
        super(batchable);
        this.relationshipType = relationshipType;
    }

    @Override
    public T getDiscriminator() {
        return relationshipType;
    }
}
