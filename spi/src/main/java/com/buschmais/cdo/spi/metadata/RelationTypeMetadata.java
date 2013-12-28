package com.buschmais.cdo.spi.metadata;

import com.buschmais.cdo.spi.reflection.AnnotatedType;

import java.util.Collection;
import java.util.Collections;

public class RelationTypeMetadata<DatastoreMetadata> extends AbstractMetadata<RelationTypeMetadata, DatastoreMetadata> {

    public enum Direction {
        INCOMING,
        OUTGOING;
    }

    public RelationTypeMetadata(AnnotatedType annotatedType, Collection<RelationTypeMetadata> superTypes, Collection<MethodMetadata> properties, DatastoreMetadata datastoreMetadata) {
        super(annotatedType, superTypes, properties, datastoreMetadata);
    }

    public RelationTypeMetadata(DatastoreMetadata datastoreMetadata) {
        this(null, Collections.<RelationTypeMetadata>emptyList(), Collections.<MethodMetadata>emptyList(), datastoreMetadata);
    }

}
