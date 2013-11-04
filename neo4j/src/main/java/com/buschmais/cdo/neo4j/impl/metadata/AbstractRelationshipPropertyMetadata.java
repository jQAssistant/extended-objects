package com.buschmais.cdo.neo4j.impl.metadata;

import org.neo4j.graphdb.RelationshipType;

public abstract class AbstractRelationshipPropertyMetadata extends AbstractPropertyMetadata {

    private RelationshipType relationshipType;

    public AbstractRelationshipPropertyMetadata(NodeMetadataProvider.BeanProperty beanProperty, RelationshipType relationshipType) {
        super(beanProperty);
        this.relationshipType = relationshipType;
    }

    public RelationshipType getRelationshipType() {
        return relationshipType;
    }
}
