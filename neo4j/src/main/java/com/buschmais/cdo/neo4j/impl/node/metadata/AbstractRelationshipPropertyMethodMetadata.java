package com.buschmais.cdo.neo4j.impl.node.metadata;

import com.buschmais.cdo.neo4j.impl.common.reflection.BeanPropertyMethod;
import org.neo4j.graphdb.RelationshipType;

import static com.buschmais.cdo.neo4j.impl.node.metadata.RelationshipMetadata.Direction;

public abstract class AbstractRelationshipPropertyMethodMetadata<DatastoreMetadata> extends AbstractPropertyMethodMetadata<DatastoreMetadata> {

    private RelationshipMetadata relationshipType;

    private Direction direction;

    public AbstractRelationshipPropertyMethodMetadata(BeanPropertyMethod beanPropertyMethod, RelationshipMetadata relationshipMetadata, Direction direction) {
        super(beanPropertyMethod);
        this.relationshipType = relationshipMetadata;
        this.direction = direction;
    }

    public RelationshipMetadata getRelationshipMetadata() {
        return relationshipType;
    }

    public Direction getDirection() {
        return direction;
    }
}
