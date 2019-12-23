package com.buschmais.xo.spi.metadata.method;

import static com.buschmais.xo.spi.metadata.type.RelationTypeMetadata.Direction;

import com.buschmais.xo.spi.metadata.type.RelationTypeMetadata;
import com.buschmais.xo.spi.reflection.PropertyMethod;

public abstract class AbstractEntityRelationMethodMetadata<DatastoreMetadata> extends AbstractRelationPropertyMethodMetadata<DatastoreMetadata> {

    private final Class<?> elementType;

    protected AbstractEntityRelationMethodMetadata(PropertyMethod propertyMethod, RelationTypeMetadata relationshipType, Direction direction, Class<?> elementType,
                                                   DatastoreMetadata datastoreMetadata) {
        super(propertyMethod, relationshipType, direction, datastoreMetadata);
        this.elementType = elementType;
    }

    public Class<?> getElementType() {
        return elementType;
    }
}
