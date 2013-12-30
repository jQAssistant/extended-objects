package com.buschmais.cdo.spi.metadata.method;

import com.buschmais.cdo.spi.metadata.type.RelationTypeMetadata;
import com.buschmais.cdo.spi.reflection.PropertyMethod;

import static com.buschmais.cdo.spi.metadata.type.RelationTypeMetadata.Direction;

public abstract class AbstractRelationPropertyMethodMetadata<DatastoreMetadata> extends AbstractPropertyMethodMetadata<DatastoreMetadata> {

    private RelationTypeMetadata relationTypeMetadata;

    private Direction direction;

    public AbstractRelationPropertyMethodMetadata(PropertyMethod propertyMethod, RelationTypeMetadata relationTypeMetadata, Direction direction, DatastoreMetadata datastoreMetadata) {
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
