package com.buschmais.xo.spi.datastore;

import static java.util.Arrays.asList;

import java.util.HashSet;

import com.buschmais.xo.api.CompositeObject;
import com.buschmais.xo.api.CompositeType;
import com.buschmais.xo.spi.metadata.CompositeTypeBuilder;
import com.buschmais.xo.spi.metadata.type.DatastoreTypeMetadata;

/**
 * Represents a set of type metadata.
 */
public final class TypeMetadataSet<TypeMetadata extends DatastoreTypeMetadata<?>> extends HashSet<TypeMetadata> {

    private boolean containsAbstractType = false;

    private boolean containsFinalType = false;

    /**
     * Add a type metadata.
     * 
     * @param typeMetadata
     *            The type metadata.
     * @return <code>true</code> if the metadata could be added.
     */
    public boolean add(TypeMetadata typeMetadata) {
        containsAbstractType = containsAbstractType || typeMetadata.isAbstract();
        containsFinalType = containsFinalType || typeMetadata.isFinal();
        return super.add(typeMetadata);
    }

    /**
     * Convert this set into a composite type.
     * 
     * @return The composite type.
     */
    public CompositeType getCompositeType() {
        return CompositeTypeBuilder.create(CompositeObject.class, this, t -> t.getAnnotatedType().getAnnotatedElement());
    }

    /**
     * Determine if at least one of the contained types is abstract.
     *
     * @return <code>true</code> if at least one of the contained types is
     *         abstract.
     */
    public boolean containsAbstractType() {
        return containsAbstractType;
    }

    /**
     * Determine if at least one of the contained types is final.
     *
     * @return <code>true</code> if at least one of the contained types is
     *         final.
     */
    public boolean containsFinalType() {
        return containsFinalType;
    }

    @Override
    public String toString() {
        return asList(CompositeObject.class).toString();
    }
}
