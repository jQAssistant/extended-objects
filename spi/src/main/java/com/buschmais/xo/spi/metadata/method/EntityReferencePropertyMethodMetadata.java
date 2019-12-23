package com.buschmais.xo.spi.metadata.method;

import com.buschmais.xo.spi.metadata.type.RelationTypeMetadata;
import com.buschmais.xo.spi.reflection.PropertyMethod;

public class EntityReferencePropertyMethodMetadata<DatastoreMetadata> extends AbstractEntityRelationMethodMetadata<DatastoreMetadata> {

    public EntityReferencePropertyMethodMetadata(PropertyMethod propertyMethod, RelationTypeMetadata relationshipType, RelationTypeMetadata.Direction direction,
            Class<?> elementType, DatastoreMetadata datastoreMetadata) {
        super(propertyMethod, relationshipType, direction, elementType, datastoreMetadata);
    }

}
