package com.buschmais.cdo.neo4j.impl.node.metadata;

import com.buschmais.cdo.neo4j.impl.common.reflection.BeanPropertyMethod;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.RelationshipType;

public class CollectionPropertyMethodMetadata extends AbstractRelationshipPropertyMethodMetadata {

    public CollectionPropertyMethodMetadata(BeanPropertyMethod beanPropertyMethod, RelationshipType relationshipType, Direction direction) {
        super(beanPropertyMethod, relationshipType, direction);
    }

}
