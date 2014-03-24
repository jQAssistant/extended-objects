package com.buschmais.xo.spi.metadata.method;

import com.buschmais.xo.spi.metadata.type.RelationTypeMetadata;
import com.buschmais.xo.spi.reflection.PropertyMethod;

public class EntityReferencePropertyMethodMetadata<DatastoreMetadata> extends AbstractRelationPropertyMethodMetadata<DatastoreMetadata> {

    public EntityReferencePropertyMethodMetadata(PropertyMethod propertyMethod, RelationTypeMetadata relationshipType, RelationTypeMetadata.Direction direction, DatastoreMetadata datastoreMetadata) {
        super(propertyMethod, relationshipType, direction, datastoreMetadata);
    }

}
