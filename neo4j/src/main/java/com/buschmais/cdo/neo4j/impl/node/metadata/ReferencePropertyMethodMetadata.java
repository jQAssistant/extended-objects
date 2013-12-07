package com.buschmais.cdo.neo4j.impl.node.metadata;

import com.buschmais.cdo.neo4j.impl.common.reflection.BeanPropertyMethod;

public class ReferencePropertyMethodMetadata extends AbstractRelationshipPropertyMethodMetadata {

    private final BeanPropertyMethod beanPropertyMethod;
    private final RelationshipMetadata relationshipType;
    private final RelationshipMetadata.Direction direction;

    public ReferencePropertyMethodMetadata(BeanPropertyMethod beanPropertyMethod, RelationshipMetadata relationshipType, RelationshipMetadata.Direction direction) {
        super(beanPropertyMethod, relationshipType, direction);
        this.beanPropertyMethod = beanPropertyMethod;
        this.relationshipType = relationshipType;
        this.direction = direction;
    }

}
