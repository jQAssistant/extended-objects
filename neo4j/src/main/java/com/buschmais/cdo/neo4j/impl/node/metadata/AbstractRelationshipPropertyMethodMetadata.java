package com.buschmais.cdo.neo4j.impl.node.metadata;

import com.buschmais.cdo.neo4j.impl.common.reflection.BeanPropertyMethod;
import org.neo4j.graphdb.RelationshipType;

public abstract class AbstractRelationshipPropertyMethodMetadata extends AbstractPropertyMethodMetadata {

    private RelationshipType relationshipType;

    public AbstractRelationshipPropertyMethodMetadata(BeanPropertyMethod beanPropertyMethod, RelationshipType relationshipType) {
        super(beanPropertyMethod);
        this.relationshipType = relationshipType;
    }

    public RelationshipType getRelationshipType() {
        return relationshipType;
    }
}
