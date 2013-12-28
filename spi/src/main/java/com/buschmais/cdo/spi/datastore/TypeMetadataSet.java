package com.buschmais.cdo.spi.datastore;

import com.buschmais.cdo.spi.metadata.EntityTypeMetadata;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * Represents a set of entity type metadata.
 */
public final class TypeMetadataSet<EntityMetadata extends DatastoreEntityMetadata<?>> extends TreeSet<EntityTypeMetadata<EntityMetadata>> {

    /**
     * Constructor.
     */
    public TypeMetadataSet() {
        super(new Comparator<EntityTypeMetadata<?>>() {
            @Override
            public int compare(EntityTypeMetadata<?> o1, EntityTypeMetadata<?> o2) {
                return o1.getAnnotatedType().getAnnotatedElement().getName().compareTo(o2.getAnnotatedType().getAnnotatedElement().getName());
            }
        });
    }

    public Set<Class<?>> toClasses() {
        Set<Class<?>> classes = new HashSet<>();
        for (EntityTypeMetadata<EntityMetadata> entityTypeMetadata : this) {
            classes.add(entityTypeMetadata.getAnnotatedType().getAnnotatedElement());
        }
        return classes;
    }
}
