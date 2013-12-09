package com.buschmais.cdo.spi.metadata;

import com.buschmais.cdo.spi.reflection.PropertyMethod;

import static com.buschmais.cdo.spi.metadata.RelationMetadata.Direction;

public class CollectionPropertyMethodMetadata<DatastoreMetadata> extends AbstractRelationPropertyMethodMetadata<DatastoreMetadata> {

    public CollectionPropertyMethodMetadata(PropertyMethod beanPropertyMethod, RelationMetadata relationshipType, Direction direction, DatastoreMetadata datastoreMetadata) {
        super(beanPropertyMethod, relationshipType, direction, datastoreMetadata);
    }

}
