package com.buschmais.xo.spi.datastore;

import com.buschmais.xo.spi.metadata.type.DatastoreTypeMetadata;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import static java.util.Arrays.asList;

/**
 * Represents a set of type metadata.
 */
public final class TypeMetadataSet<TypeMetadata extends DatastoreTypeMetadata<?>> extends TreeSet<TypeMetadata> {

    private boolean containsAbstractType = false;

    private boolean containsFinalType = false;

    /**
     * Constructor.
     */
    public TypeMetadataSet() {
        super((o1, o2) -> o1.getAnnotatedType().getAnnotatedElement().getName().compareTo(o2.getAnnotatedType().getAnnotatedElement().getName()));
    }

    public boolean add(TypeMetadata typeMetadata) {
        containsAbstractType = containsAbstractType || typeMetadata.isAbstract();
        containsFinalType = containsFinalType || typeMetadata.isFinal();
        return super.add(typeMetadata);
    }

    public Class<?>[] toClasses() {
        List<Class<?>> classes = new ArrayList<>();
        for (TypeMetadata typeMetadata : this) {
            classes.add(typeMetadata.getAnnotatedType().getAnnotatedElement());
        }
        return classes.toArray(new Class[classes.size()]);
    }

    /**
     * Determine if at least one of the contained types is abstract.
     *
     * @return <code>true</code>  if at least one of the contained types is abstract.
     */
    public boolean containsAbstractType() {
        return containsAbstractType;
    }

    /**
     * Determine if at least one of the contained types is final.
     *
     * @return <code>true</code>  if at least one of the contained types is final.
     */
    public boolean containsFinalType() {
        return containsFinalType;
    }

    @Override
    public String toString() {
        return asList(toClasses()).toString();
    }
}
