package com.buschmais.cdo.spi.metadata.method;

import com.buschmais.cdo.spi.metadata.type.RelationTypeMetadata;
import com.buschmais.cdo.spi.reflection.PropertyMethod;

import static com.buschmais.cdo.spi.metadata.type.RelationTypeMetadata.Direction;

public class CollectionPropertyMethodMetadata<DatastoreMetadata> extends AbstractRelationPropertyMethodMetadata<DatastoreMetadata> {

    public CollectionPropertyMethodMetadata(PropertyMethod propertyMethod, RelationTypeMetadata relationshipType, Direction direction, DatastoreMetadata datastoreMetadata) {
        super(propertyMethod, relationshipType, direction, datastoreMetadata);
    }

}
