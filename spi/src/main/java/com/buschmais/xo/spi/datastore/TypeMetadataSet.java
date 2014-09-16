package com.buschmais.xo.spi.datastore;

import com.buschmais.xo.spi.metadata.type.DatastoreTypeMetadata;

import java.util.*;

/**
 * Represents a set of type metadata.
 */
public final class TypeMetadataSet<TypeMetadata extends DatastoreTypeMetadata<?>> extends TreeSet<TypeMetadata> {

    /**
     * Constructor.
     */
    public TypeMetadataSet() {
        super((o1, o2) -> o1.getAnnotatedType().getAnnotatedElement().getName().compareTo(o2.getAnnotatedType().getAnnotatedElement().getName()));
    }

    public Class<?>[] toClasses() {
        List<Class<?>> classes = new ArrayList<>();
        for (TypeMetadata typeMetadata : this) {
            classes.add(typeMetadata.getAnnotatedType().getAnnotatedElement());
        }
        return classes.toArray(new Class[classes.size()]);
    }
}
