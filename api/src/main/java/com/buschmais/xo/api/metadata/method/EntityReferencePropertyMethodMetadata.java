package com.buschmais.xo.api.metadata.method;

import com.buschmais.xo.api.metadata.reflection.PropertyMethod;
import com.buschmais.xo.api.metadata.type.RelationTypeMetadata;

public class EntityReferencePropertyMethodMetadata<DatastoreMetadata> extends AbstractEntityRelationMethodMetadata<DatastoreMetadata> {

    public EntityReferencePropertyMethodMetadata(PropertyMethod propertyMethod, RelationTypeMetadata relationshipType, RelationTypeMetadata.Direction direction,
            Class<?> elementType, DatastoreMetadata datastoreMetadata) {
        super(propertyMethod, relationshipType, direction, elementType, datastoreMetadata);
    }

}
