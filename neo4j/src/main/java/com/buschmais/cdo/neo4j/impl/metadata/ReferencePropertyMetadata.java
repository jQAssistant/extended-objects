package com.buschmais.cdo.neo4j.impl.metadata;

import org.neo4j.graphdb.RelationshipType;

public class ReferencePropertyMetadata extends AbstractRelationshipPropertyMetadata {

    public ReferencePropertyMetadata(BeanProperty beanProperty, RelationshipType relationshipType) {
        super(beanProperty, relationshipType);
    }

}
