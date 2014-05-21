package com.buschmais.xo.spi.datastore;

import com.buschmais.xo.spi.metadata.method.PrimitivePropertyMethodMetadata;

/**
 * Created by dimahler on 5/21/2014.
 */
public interface DatastoreElementManager<Element, PropertyMetadata> {

    /**
     * Set the value of a primitive property.
     *
     * @param entity   The entity.
     * @param metadata The property metadata.
     * @param value    The value
     */
    void setProperty(Element entity, PrimitivePropertyMethodMetadata<PropertyMetadata> metadata, Object value);


    /**
     * Determine if the value of a primitive property is set.
     *
     * @param entity   The entity.
     * @param metadata The property metadata.
     */
    boolean hasProperty(Element entity, PrimitivePropertyMethodMetadata<PropertyMetadata> metadata);

    /**
     * Remove the value of a primitive property.
     *
     * @param entity   The entity.
     * @param metadata The property metadata.
     */
    void removeProperty(Element entity, PrimitivePropertyMethodMetadata<PropertyMetadata> metadata);

    /**
     * Get the value of a primitive property.
     *
     * @param entity   The entity.
     * @param metadata The property metadata.
     */
    Object getProperty(Element entity, PrimitivePropertyMethodMetadata<PropertyMetadata> metadata);

}
