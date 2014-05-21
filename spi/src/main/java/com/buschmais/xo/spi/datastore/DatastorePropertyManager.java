package com.buschmais.xo.spi.datastore;

import com.buschmais.xo.spi.metadata.method.PrimitivePropertyMethodMetadata;

/**
 * Defines the interface for all datastore operations related to properties.
 */
public interface DatastorePropertyManager<Element, PropertyMetadata> {

    /**
     * Set the value of a primitive property.
     * 
     * @param entity
     *            The entity.
     * @param metadata
     *            The property metadata.
     * @param value
     *            The value
     */
    void setProperty(Element entity, PrimitivePropertyMethodMetadata<PropertyMetadata> metadata, Object value);

    /**
     * Determine if the value of a primitive property is set.
     * 
     * @param entity
     *            The entity.
     * @param metadata
     *            The property metadata.
     */
    boolean hasProperty(Element entity, PrimitivePropertyMethodMetadata<PropertyMetadata> metadata);

    /**
     * Remove the value of a primitive property.
     * 
     * @param entity
     *            The entity.
     * @param metadata
     *            The property metadata.
     */
    void removeProperty(Element entity, PrimitivePropertyMethodMetadata<PropertyMetadata> metadata);

    /**
     * Get the value of a primitive property.
     * 
     * @param entity
     *            The entity.
     * @param metadata
     *            The property metadata.
     */
    Object getProperty(Element entity, PrimitivePropertyMethodMetadata<PropertyMetadata> metadata);

}
