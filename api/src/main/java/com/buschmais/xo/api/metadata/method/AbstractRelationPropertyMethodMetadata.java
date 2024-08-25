package com.buschmais.xo.api.metadata.method;

import com.buschmais.xo.api.metadata.reflection.PropertyMethod;
import com.buschmais.xo.api.metadata.type.RelationTypeMetadata;

import static com.buschmais.xo.api.metadata.type.RelationTypeMetadata.Direction;

public abstract class AbstractRelationPropertyMethodMetadata<DatastoreMetadata> extends AbstractPropertyMethodMetadata<DatastoreMetadata> {

    private final RelationTypeMetadata relationTypeMetadata;

    private final Direction direction;

    public AbstractRelationPropertyMethodMetadata(PropertyMethod propertyMethod, RelationTypeMetadata relationTypeMetadata, Direction direction,
        DatastoreMetadata datastoreMetadata) {
        super(propertyMethod, datastoreMetadata);
        this.relationTypeMetadata = relationTypeMetadata;
        this.direction = direction;
    }

    public RelationTypeMetadata getRelationshipMetadata() {
        return relationTypeMetadata;
    }

    public Direction getDirection() {
        return direction;
    }
}
