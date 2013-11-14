package com.buschmais.cdo.neo4j.impl.metadata;

import org.neo4j.graphdb.RelationshipType;

public class ReferenceMethodMetadata extends AbstractRelationshipMethodMetadata {

    public ReferenceMethodMetadata(BeanPropertyMethod beanPropertyMethod, RelationshipType relationshipType) {
        super(beanPropertyMethod, relationshipType);
    }

}
