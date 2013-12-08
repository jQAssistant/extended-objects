package com.buschmais.cdo.neo4j.impl.node.metadata;

import org.neo4j.graphdb.RelationshipType;

import java.util.Collection;
import java.util.Collections;

public class RelationshipMetadata<DatastoreMetadata> extends AbstractMetadata<DatastoreMetadata> {

    public enum Direction {
        INCOMING,
        OUTGOING;
    }

    private RelationshipType relationshipType;

    public RelationshipMetadata(Collection<AbstractMethodMetadata> properties, RelationshipType relationshipType) {
        super(properties);
        this.relationshipType = relationshipType;
    }

    public RelationshipMetadata(RelationshipType relationshipType) {
        this(Collections.<AbstractMethodMetadata>emptyList(), relationshipType);
    }

    public RelationshipType getRelationshipType() {
        return relationshipType;
    }
}
