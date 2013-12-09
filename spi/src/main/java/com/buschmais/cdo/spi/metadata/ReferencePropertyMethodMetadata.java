package com.buschmais.cdo.spi.metadata;

import com.buschmais.cdo.spi.reflection.PropertyMethod;

public class ReferencePropertyMethodMetadata<DatastoreMetadata> extends AbstractRelationPropertyMethodMetadata<DatastoreMetadata> {

    public ReferencePropertyMethodMetadata(PropertyMethod beanPropertyMethod, RelationMetadata relationshipType, RelationMetadata.Direction direction, DatastoreMetadata datastoreMetadata) {
        super(beanPropertyMethod, relationshipType, direction, datastoreMetadata);
    }

}
