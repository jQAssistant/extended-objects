package com.buschmais.cdo.neo4j.impl.metadata;

import org.neo4j.graphdb.RelationshipType;

public class CollectionPropertyMetadata extends AbstractRelationshipPropertyMetadata {

    public CollectionPropertyMetadata(BeanProperty beanProperty, RelationshipType relationshipType) {
        super(beanProperty, relationshipType);
    }

}
