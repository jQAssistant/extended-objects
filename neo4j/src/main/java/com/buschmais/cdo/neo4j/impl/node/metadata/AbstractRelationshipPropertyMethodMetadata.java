package com.buschmais.cdo.neo4j.impl.node.metadata;

import com.buschmais.cdo.neo4j.impl.common.reflection.BeanPropertyMethod;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.RelationshipType;

public abstract class AbstractRelationshipPropertyMethodMetadata extends AbstractPropertyMethodMetadata {

    private RelationshipType relationshipType;

    private Direction direction;

    public AbstractRelationshipPropertyMethodMetadata(BeanPropertyMethod beanPropertyMethod, RelationshipType relationshipType, Direction direction) {
        super(beanPropertyMethod);
        this.relationshipType = relationshipType;
        this.direction = direction;
    }

    public RelationshipType getRelationshipType() {
        return relationshipType;
    }

    public Direction getDirection() {
        return direction;
    }
}
