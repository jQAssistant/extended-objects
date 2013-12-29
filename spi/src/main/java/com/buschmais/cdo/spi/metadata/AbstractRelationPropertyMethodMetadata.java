package com.buschmais.cdo.spi.metadata;

import com.buschmais.cdo.spi.reflection.PropertyMethod;

import static com.buschmais.cdo.spi.metadata.RelationMetadata.Direction;

public abstract class AbstractRelationPropertyMethodMetadata<DatastoreMetadata> extends AbstractPropertyMethodMetadata<DatastoreMetadata> {

    private final RelationMetadata relationMetadata;

    private final Direction direction;

    public AbstractRelationPropertyMethodMetadata(PropertyMethod propertyMethod, RelationMetadata relationMetadata, Direction direction, DatastoreMetadata datastoreMetadata) {
        super(propertyMethod, datastoreMetadata);
        this.relationMetadata = relationMetadata;
        this.direction = direction;
    }

    public RelationMetadata getRelationshipMetadata() {
        return relationMetadata;
    }

    public Direction getDirection() {
        return direction;
    }
}
