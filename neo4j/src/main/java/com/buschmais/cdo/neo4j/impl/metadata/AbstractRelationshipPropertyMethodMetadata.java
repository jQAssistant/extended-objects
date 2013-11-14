package com.buschmais.cdo.neo4j.impl.metadata;

import org.neo4j.graphdb.RelationshipType;

public abstract class AbstractRelationshipPropertyMethodMetadata extends AbstractPropertyMethodMetadata {

    private RelationshipType relationshipType;

    public AbstractRelationshipPropertyMethodMetadata(BeanPropertyMethod beanPropertyMethod, RelationshipType relationshipType) {
        super(beanPropertyMethod);
        this.relationshipType = relationshipType;
    }

    public RelationshipType getRelationshipType() {
        return relationshipType;
    }
}
