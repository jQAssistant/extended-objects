package com.buschmais.cdo.spi.metadata.type;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.spi.metadata.method.MethodMetadata;
import com.buschmais.cdo.spi.reflection.AnnotatedType;

import java.util.Collection;
import java.util.Collections;

public class RelationTypeMetadata<DatastoreMetadata> extends AbstractDatastoreTypeMetadata<DatastoreMetadata> {

    public enum Direction {
        FROM,
        TO;

        public CdoException createNotSupportedException() {
            return new CdoException("Relation direction '" + name() + "' is not supported.");
        }
    }

    private final Class<?> fromType;
    private final Class<?> toType;

    public RelationTypeMetadata(AnnotatedType annotatedType, Collection<TypeMetadata> superTypes, Collection<MethodMetadata<?, ?>> properties, Class<?> fromType, Class<?> toType, DatastoreMetadata datastoreMetadata) {
        super(annotatedType, superTypes, properties, null, datastoreMetadata);
        this.fromType = fromType;
        this.toType = toType;
    }

    public RelationTypeMetadata(DatastoreMetadata datastoreMetadata) {
        this(null, Collections.<TypeMetadata>emptyList(), Collections.<MethodMetadata<?, ?>>emptyList(), null, null, datastoreMetadata);
    }

    public Class<?> getFromType() {
        return fromType;
    }

    public Class<?> getToType() {
        return toType;
    }

}
