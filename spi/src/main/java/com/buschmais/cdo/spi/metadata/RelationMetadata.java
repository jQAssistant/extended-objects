package com.buschmais.cdo.spi.metadata;

import com.buschmais.cdo.spi.reflection.AnnotatedType;

import java.util.Collection;
import java.util.Collections;

public class RelationMetadata<DatastoreMetadata> extends AbstractMetadata<RelationMetadata, DatastoreMetadata> {

    public enum Direction {
        INCOMING,
        OUTGOING;
    }

    public RelationMetadata(AnnotatedType annotatedType, Collection<RelationMetadata> superTypes, Collection<AbstractMethodMetadata> properties, DatastoreMetadata datastoreMetadata) {
        super(annotatedType, superTypes, properties, datastoreMetadata);
    }

    public RelationMetadata(DatastoreMetadata datastoreMetadata) {
        this(null, Collections.<RelationMetadata>emptyList(), Collections.<AbstractMethodMetadata>emptyList(), datastoreMetadata);
    }

}
