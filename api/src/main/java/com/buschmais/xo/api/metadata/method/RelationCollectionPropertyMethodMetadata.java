package com.buschmais.xo.api.metadata.method;

import static com.buschmais.xo.api.metadata.type.RelationTypeMetadata.Direction;

import com.buschmais.xo.api.metadata.reflection.PropertyMethod;
import com.buschmais.xo.api.metadata.type.RelationTypeMetadata;

public class RelationCollectionPropertyMethodMetadata<DatastoreMetadata> extends AbstractRelationPropertyMethodMetadata<DatastoreMetadata> {

    public RelationCollectionPropertyMethodMetadata(PropertyMethod propertyMethod, RelationTypeMetadata relationshipType, Direction direction,
            DatastoreMetadata datastoreMetadata) {
        super(propertyMethod, relationshipType, direction, datastoreMetadata);
    }

}
