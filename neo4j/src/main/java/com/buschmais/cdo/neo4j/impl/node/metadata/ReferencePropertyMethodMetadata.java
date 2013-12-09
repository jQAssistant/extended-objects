package com.buschmais.cdo.neo4j.impl.node.metadata;

import com.buschmais.cdo.neo4j.impl.common.reflection.PropertyMethod;

public class ReferencePropertyMethodMetadata<DatastoreMetadata> extends AbstractRelationPropertyMethodMetadata<DatastoreMetadata> {

    public ReferencePropertyMethodMetadata(PropertyMethod beanPropertyMethod, RelationMetadata relationshipType, RelationMetadata.Direction direction, DatastoreMetadata datastoreMetadata) {
        super(beanPropertyMethod, relationshipType, direction, datastoreMetadata);
    }

}
