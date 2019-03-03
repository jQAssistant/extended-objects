package com.buschmais.xo.spi.metadata.method;

import static com.buschmais.xo.spi.metadata.type.RelationTypeMetadata.Direction;

import com.buschmais.xo.spi.metadata.type.RelationTypeMetadata;
import com.buschmais.xo.spi.reflection.PropertyMethod;

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
