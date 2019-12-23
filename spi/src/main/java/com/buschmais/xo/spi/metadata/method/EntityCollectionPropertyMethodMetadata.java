package com.buschmais.xo.spi.metadata.method;

import static com.buschmais.xo.spi.metadata.type.RelationTypeMetadata.Direction;

import com.buschmais.xo.spi.metadata.type.RelationTypeMetadata;
import com.buschmais.xo.spi.reflection.PropertyMethod;

public class EntityCollectionPropertyMethodMetadata<DatastoreMetadata> extends AbstractEntityRelationMethodMetadata<DatastoreMetadata> {

    public EntityCollectionPropertyMethodMetadata(PropertyMethod propertyMethod, RelationTypeMetadata relationshipType, Direction direction,
            Class<?> elementType, DatastoreMetadata datastoreMetadata) {
        super(propertyMethod, relationshipType, direction, elementType, datastoreMetadata);
    }

}
