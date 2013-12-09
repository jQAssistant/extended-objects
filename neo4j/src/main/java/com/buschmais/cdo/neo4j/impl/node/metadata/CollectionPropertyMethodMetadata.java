package com.buschmais.cdo.neo4j.impl.node.metadata;

import com.buschmais.cdo.neo4j.impl.common.reflection.PropertyMethod;

import static com.buschmais.cdo.neo4j.impl.node.metadata.RelationMetadata.Direction;

public class CollectionPropertyMethodMetadata<DatastoreMetadata> extends AbstractRelationPropertyMethodMetadata<DatastoreMetadata> {

    public CollectionPropertyMethodMetadata(PropertyMethod beanPropertyMethod, RelationMetadata relationshipType, Direction direction, DatastoreMetadata datastoreMetadata) {
        super(beanPropertyMethod, relationshipType, direction, datastoreMetadata);
    }

}
