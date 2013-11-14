package com.buschmais.cdo.neo4j.impl.metadata;

import org.neo4j.graphdb.RelationshipType;

public class CollectionMethodMetadata extends AbstractRelationshipMethodMetadata {

    public CollectionMethodMetadata(BeanPropertyMethod beanPropertyMethod, RelationshipType relationshipType) {
        super(beanPropertyMethod, relationshipType);
    }

}
