package com.buschmais.xo.api;

/**
 * Defines the interface for a type migrator.
 */
public interface XOMigrator {
    /**
     * Add a type to the instance.
     *
     * @param newType
     *            The new type.
     * @param newTypes
     *            The new types.
     * @return A new instance representing the original types and the given new
     *         types.
     */
    CompositeObject add(Class<?> newType, Class<?>... newTypes);

    /**
     * Remove a type from the instance.
     *
     * @param obsoleteType
     *            The obsolete type.
     * @param obsoleteTypes
     *            The obsolete types.
     * @return A new instance without the obsolete types.
     */
    CompositeObject remove(Class<?> obsoleteType, Class<?>... obsoleteTypes);
}
