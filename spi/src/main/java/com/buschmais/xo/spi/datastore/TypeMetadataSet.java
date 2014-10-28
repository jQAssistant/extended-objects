package com.buschmais.xo.spi.datastore;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import com.buschmais.xo.spi.metadata.type.DatastoreTypeMetadata;

/**
 * Represents a set of type metadata.
 */
public final class TypeMetadataSet<TypeMetadata extends DatastoreTypeMetadata<?>> extends TreeSet<TypeMetadata> {

    private boolean abstractType = false;

    private boolean finalType = false;

    /**
     * Constructor.
     */
    public TypeMetadataSet() {
        super((o1, o2) -> o1.getAnnotatedType().getAnnotatedElement().getName().compareTo(o2.getAnnotatedType().getAnnotatedElement().getName()));
    }

    public boolean add(TypeMetadata typeMetadata) {
        abstractType = abstractType || typeMetadata.isAbstract();
        finalType = finalType || typeMetadata.isFinal();
        return super.add(typeMetadata);
    }

    public Class<?>[] toClasses() {
        List<Class<?>> classes = new ArrayList<>();
        for (TypeMetadata typeMetadata : this) {
            classes.add(typeMetadata.getAnnotatedType().getAnnotatedElement());
        }
        return classes.toArray(new Class[classes.size()]);
    }
}
