package com.buschmais.cdo.spi.datastore;

import com.buschmais.cdo.spi.metadata.TypeMetadata;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * Represents a set of entity type metadata.
 */
public final class TypeMetadataSet<EntityMetadata extends DatastoreEntityMetadata<?>> extends TreeSet<TypeMetadata<EntityMetadata>> {

    /**
     * Constructor.
     */
    public TypeMetadataSet() {
        super(new Comparator<TypeMetadata<?>>() {
            @Override
            public int compare(TypeMetadata<?> o1, TypeMetadata<?> o2) {
                return o1.getAnnotatedType().getAnnotatedElement().getName().compareTo(o2.getAnnotatedType().getAnnotatedElement().getName());
            }
        });
    }

    public Set<Class<?>> toClasses() {
        Set<Class<?>> classes = new HashSet<>();
        for (TypeMetadata<EntityMetadata> typeMetadata : this) {
            classes.add(typeMetadata.getAnnotatedType().getAnnotatedElement());
        }
        return classes;
    }
}
