package com.buschmais.xo.impl.proxy.entity.property;

import java.util.Collection;

import com.buschmais.xo.api.XOException;

/**
 * Defines the collection types supported for properties.
 */
public enum CollectionPropertyType {

    LIST(java.util.List.class), SET(java.util.Set.class), COLLECTION(java.util.Collection.class);

    private Class<? extends Collection> collectionType;

    /**
     * Constructor.
     *
     * @param type
     *            The collection type (i.e. interface).
     */
    private CollectionPropertyType(Class<? extends java.util.Collection> type) {
        this.collectionType = type;
    }

    /**
     * Return the Java collection type.
     *
     * @return The Java collection type.
     */
    public Class<? extends Collection> getCollectionType() {
        return collectionType;
    }

    /**
     * Determine the
     * {@link com.buschmais.xo.impl.proxy.entity.property.CollectionPropertyType}
     * for a given class.
     *
     * @param type
     *            The class.
     * @return The collection property type.
     */
    public static CollectionPropertyType getCollectionPropertyType(Class<?> type) {
        for (CollectionPropertyType collectionPropertyType : CollectionPropertyType.values()) {
            if (collectionPropertyType.collectionType.isAssignableFrom(type)) {
                return collectionPropertyType;
            }
        }
        throw new XOException(type.getName() + "is not a supported collection type.");
    }

}
