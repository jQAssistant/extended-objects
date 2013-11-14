package com.buschmais.cdo.neo4j.impl.metadata;

import org.neo4j.graphdb.RelationshipType;

public class ReferencePropertyMethodMetadata extends AbstractRelationshipPropertyMethodMetadata {

    public ReferencePropertyMethodMetadata(BeanPropertyMethod beanPropertyMethod, RelationshipType relationshipType) {
        super(beanPropertyMethod, relationshipType);
    }

}
