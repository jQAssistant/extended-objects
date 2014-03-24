package com.buschmais.xo.spi.datastore;

import com.buschmais.xo.spi.metadata.type.DatastoreTypeMetadata;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * Represents a set of type metadata.
 */
public final class TypeMetadataSet<TypeMetadata extends DatastoreTypeMetadata<?>> extends TreeSet<TypeMetadata> {

    /**
     * Constructor.
     */
    public TypeMetadataSet() {
        super(new Comparator<TypeMetadata>() {
            @Override
            public int compare(TypeMetadata o1, TypeMetadata o2) {
                return o1.getAnnotatedType().getAnnotatedElement().getName().compareTo(o2.getAnnotatedType().getAnnotatedElement().getName());
            }
        });
    }

    public Set<Class<?>> toClasses() {
        Set<Class<?>> classes = new HashSet<>();
        for (TypeMetadata typeMetadata : this) {
            classes.add(typeMetadata.getAnnotatedType().getAnnotatedElement());
        }
        return classes;
    }
}
