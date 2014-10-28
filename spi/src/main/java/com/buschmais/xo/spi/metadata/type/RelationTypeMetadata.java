package com.buschmais.xo.spi.metadata.type;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.spi.metadata.method.MethodMetadata;
import com.buschmais.xo.spi.reflection.AnnotatedType;

import java.util.Collection;
import java.util.Collections;

/**
 * Represents metadata for entity types.
 *
 * @param <DatastoreMetadata> The datastore specific metadata type.
 */
public class RelationTypeMetadata<DatastoreMetadata> extends AbstractDatastoreTypeMetadata<DatastoreMetadata> {

    /**
     * Defines the allowed directions..
     */
    public enum Direction {
        FROM,
        TO;

        public XOException createNotSupportedException() {
            return new XOException("Relation direction '" + name() + "' is not supported.");
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

    @Override
    public boolean isAbstract() {
        return false;
    }

    @Override
    public boolean isFinal() {
        return true;
    }
}
