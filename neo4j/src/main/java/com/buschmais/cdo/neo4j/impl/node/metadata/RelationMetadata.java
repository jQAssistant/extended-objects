package com.buschmais.cdo.neo4j.impl.node.metadata;

import java.util.Collection;
import java.util.Collections;

public class RelationMetadata<DatastoreMetadata> extends AbstractMetadata<DatastoreMetadata> {

    public enum Direction {
        INCOMING,
        OUTGOING;
    }

    public RelationMetadata(Collection<AbstractMethodMetadata> properties, DatastoreMetadata datastoreMetadata) {
        super(properties, datastoreMetadata);
    }

    public RelationMetadata(DatastoreMetadata datastoreMetadata) {
        this(Collections.<AbstractMethodMetadata>emptyList(), datastoreMetadata);
    }

}
