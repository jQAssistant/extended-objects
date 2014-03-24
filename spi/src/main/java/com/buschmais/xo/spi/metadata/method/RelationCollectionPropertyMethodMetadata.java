package com.buschmais.xo.spi.metadata.method;

import com.buschmais.xo.spi.metadata.type.RelationTypeMetadata;
import com.buschmais.xo.spi.reflection.PropertyMethod;

import static com.buschmais.xo.spi.metadata.type.RelationTypeMetadata.Direction;

public class RelationCollectionPropertyMethodMetadata<DatastoreMetadata> extends AbstractRelationPropertyMethodMetadata<DatastoreMetadata> {

    public RelationCollectionPropertyMethodMetadata(PropertyMethod propertyMethod, RelationTypeMetadata relationshipType, Direction direction, DatastoreMetadata datastoreMetadata) {
        super(propertyMethod, relationshipType, direction, datastoreMetadata);
    }

}
