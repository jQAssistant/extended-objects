package com.buschmais.cdo.spi.metadata;

import com.buschmais.cdo.spi.reflection.PropertyMethod;

public class ReferencePropertyMethodMetadata<DatastoreMetadata> extends AbstractRelationPropertyMethodMetadata<DatastoreMetadata> {

    public ReferencePropertyMethodMetadata(PropertyMethod propertyMethod, RelationTypeMetadata relationshipType, RelationTypeMetadata.Direction direction, DatastoreMetadata datastoreMetadata) {
        super(propertyMethod, relationshipType, direction, datastoreMetadata);
    }

}
