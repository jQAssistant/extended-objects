package com.buschmais.cdo.neo4j.impl.node.metadata;

import com.buschmais.cdo.neo4j.impl.common.reflection.BeanPropertyMethod;
import org.neo4j.graphdb.RelationshipType;

public class ReferencePropertyMethodMetadata extends AbstractRelationshipPropertyMethodMetadata {

    public ReferencePropertyMethodMetadata(BeanPropertyMethod beanPropertyMethod, RelationshipType relationshipType) {
        super(beanPropertyMethod, relationshipType);
    }

}