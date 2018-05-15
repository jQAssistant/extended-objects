package com.buschmais.xo.spi.datastore;

import java.util.Collection;
import java.util.HashSet;

import com.buschmais.xo.api.CompositeObject;
import com.buschmais.xo.api.CompositeType;
import com.buschmais.xo.spi.metadata.CompositeTypeBuilder;
import com.buschmais.xo.spi.metadata.type.DatastoreTypeMetadata;

/**
 * Represents a set of type metadata.
 */
public final class TypeMetadataSet<TypeMetadata extends DatastoreTypeMetadata<?>> extends HashSet<TypeMetadata> {

    private boolean isFinal = false;

    /**
     * Add a type metadata.
     *
     * @param typeMetadata
     *            The type metadata.
     * @return <code>true</code> if the metadata could be added.
     */
    @Override
    public boolean add(TypeMetadata typeMetadata) {
        isFinal = isFinal || typeMetadata.isFinal();
        return super.add(typeMetadata);
    }

    /**
     * Determines if any given element in another {@link Collection} is contained in
     * this set.
     *
     * @param other
     *            The other {@link Collection}.
     * @return <code>true</code> if any other element is contained.
     */
    public boolean containsAny(Collection<TypeMetadata> other) {
        for (TypeMetadata typeMetadata : other) {
            if (contains(typeMetadata)) {
                return true;
            }
        }
        return false;
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
     * @return <code>true</code> if at least one of the contained types is abstract.
     */
    public boolean isAbstract() {
        for (TypeMetadata typeMetadata : this) {
            if (!typeMetadata.isAbstract()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Determine if at least one of the contained types is final.
     *
     * @return <code>true</code> if at least one of the contained types is final.
     */
    public boolean isFinal() {
        return isFinal;
    }

}
