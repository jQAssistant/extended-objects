package com.buschmais.cdo.spi.metadata.type;

import com.buschmais.cdo.spi.metadata.method.MethodMetadata;
import com.buschmais.cdo.spi.reflection.AnnotatedType;

import java.util.Collection;
import java.util.Collections;

public class RelationTypeMetadata<DatastoreMetadata> extends AbstractDatastoreTypeMetadata<DatastoreMetadata> {

    public enum Direction {
        INCOMING,
        OUTGOING;
    }

    public RelationTypeMetadata(AnnotatedType annotatedType, Collection<TypeMetadata> superTypes, Collection<MethodMetadata<?, ?>> properties, DatastoreMetadata datastoreMetadata) {
        super(annotatedType, superTypes, properties, datastoreMetadata);
    }

    public RelationTypeMetadata(DatastoreMetadata datastoreMetadata) {
        this(null, Collections.<TypeMetadata>emptyList(), Collections.<MethodMetadata<?, ?>>emptyList(), datastoreMetadata);
    }

}
