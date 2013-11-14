package com.buschmais.cdo.neo4j.impl.metadata;

import org.neo4j.graphdb.RelationshipType;

public abstract class AbstractRelationshipMethodMetadata extends AbstractMethodMetadata {

    private RelationshipType relationshipType;

    public AbstractRelationshipMethodMetadata(BeanPropertyMethod beanPropertyMethod, RelationshipType relationshipType) {
        super(beanPropertyMethod);
        this.relationshipType = relationshipType;
    }

    public RelationshipType getRelationshipType() {
        return relationshipType;
    }
}
