package com.buschmais.cdo.spi.metadata;

import java.util.Collection;
import java.util.Collections;

public class RelationMetadata<DatastoreMetadata> extends AbstractMetadata<RelationMetadata, DatastoreMetadata> {

    public enum Direction {
        INCOMING,
        OUTGOING;
    }

    public RelationMetadata(Collection<RelationMetadata> superTypes, Collection<AbstractMethodMetadata> properties, DatastoreMetadata datastoreMetadata) {
        super(superTypes, properties, datastoreMetadata);
    }

    public RelationMetadata(DatastoreMetadata datastoreMetadata) {
        this(Collections.<RelationMetadata>emptyList(), Collections.<AbstractMethodMetadata>emptyList(), datastoreMetadata);
    }

}
