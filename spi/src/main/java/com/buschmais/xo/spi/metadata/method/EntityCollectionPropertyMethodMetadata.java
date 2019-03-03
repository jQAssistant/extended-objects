package com.buschmais.xo.spi.metadata.method;

import static com.buschmais.xo.spi.metadata.type.RelationTypeMetadata.Direction;

import com.buschmais.xo.spi.metadata.type.RelationTypeMetadata;
import com.buschmais.xo.spi.reflection.PropertyMethod;

public class EntityCollectionPropertyMethodMetadata<DatastoreMetadata> extends AbstractRelationPropertyMethodMetadata<DatastoreMetadata> {

    public EntityCollectionPropertyMethodMetadata(PropertyMethod propertyMethod, RelationTypeMetadata relationshipType, Direction direction,
            DatastoreMetadata datastoreMetadata) {
        super(propertyMethod, relationshipType, direction, datastoreMetadata);
    }

}
