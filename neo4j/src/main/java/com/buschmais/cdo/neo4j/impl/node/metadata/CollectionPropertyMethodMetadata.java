package com.buschmais.cdo.neo4j.impl.node.metadata;

import com.buschmais.cdo.neo4j.impl.common.reflection.BeanPropertyMethod;

import static com.buschmais.cdo.neo4j.impl.node.metadata.RelationshipMetadata.Direction;

public class CollectionPropertyMethodMetadata extends AbstractRelationshipPropertyMethodMetadata {

    public CollectionPropertyMethodMetadata(BeanPropertyMethod beanPropertyMethod, RelationshipMetadata relationshipType, Direction direction) {
        super(beanPropertyMethod, relationshipType, direction);
    }

}
