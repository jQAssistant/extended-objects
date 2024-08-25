package com.buschmais.xo.api.metadata.method;

import com.buschmais.xo.api.metadata.reflection.PropertyMethod;
import com.buschmais.xo.api.metadata.type.RelationTypeMetadata;

import static com.buschmais.xo.api.metadata.type.RelationTypeMetadata.Direction;

public abstract class AbstractEntityRelationMethodMetadata<DatastoreMetadata> extends AbstractRelationPropertyMethodMetadata<DatastoreMetadata> {

    private final Class<?> elementType;

    protected AbstractEntityRelationMethodMetadata(PropertyMethod propertyMethod, RelationTypeMetadata relationshipType, Direction direction,
        Class<?> elementType, DatastoreMetadata datastoreMetadata) {
        super(propertyMethod, relationshipType, direction, datastoreMetadata);
        this.elementType = elementType;
    }

    public Class<?> getElementType() {
        return elementType;
    }
}
