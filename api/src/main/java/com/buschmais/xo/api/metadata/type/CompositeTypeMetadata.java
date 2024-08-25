package com.buschmais.xo.api.metadata.type;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.buschmais.xo.api.CompositeObject;

/**
 * Represents a set of dynamic type metadata used a runtime.
 */
public final class CompositeTypeMetadata<TypeMetadata extends DatastoreTypeMetadata<?>> {

    private final Set<TypeMetadata> metadata;

    private final boolean isFinal;

    private final boolean isAbstract;

    public CompositeTypeMetadata(TypeMetadata... metadata) {
        this(new HashSet<>(Arrays.asList(metadata)));
    }

    public CompositeTypeMetadata(Set<TypeMetadata> metadata) {
        this.metadata = Collections.unmodifiableSet(metadata);
        this.isFinal = metadata.stream()
            .anyMatch(typeMetadata -> typeMetadata.isFinal());
        this.isAbstract = metadata.stream()
            .allMatch(typeMetadata -> typeMetadata.isAbstract());
    }

    public Set<TypeMetadata> getMetadata() {
        return metadata;
    }

    /**
     * Convert this set into a composite type.
     *
     * @return The composite type.
     */
    public CompositeType getCompositeType() {
        CompositeType.Builder builder = CompositeType.builder()
            .type(CompositeObject.class);
        for (TypeMetadata typeMetadata : metadata) {
            builder.type(typeMetadata.getAnnotatedType()
                .getAnnotatedElement());
        }
        return builder.build();
    }

    /**
     * Determine if at least one of the contained types is abstract.
     *
     * @return <code>true</code> if at least one of the contained types is abstract.
     */
    public boolean isAbstract() {
        return isAbstract;
    }

    /**
     * Determine if at least one of the contained types is final.
     *
     * @return <code>true</code> if at least one of the contained types is final.
     */
    public boolean isFinal() {
        return isFinal;
    }

    @Override
    public String toString() {
        return "DynamicType{" + "metadata=" + metadata + ", isFinal=" + isFinal + '}';
    }

}
