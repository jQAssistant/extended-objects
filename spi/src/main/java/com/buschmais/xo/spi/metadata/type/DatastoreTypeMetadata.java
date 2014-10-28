package com.buschmais.xo.spi.metadata.type;

/**
 * Defines metadata for datastore types.
 *
 * @param <DatastoreMetadata> The datastore specific metadata type.
 */
public interface DatastoreTypeMetadata<DatastoreMetadata> extends TypeMetadata {

    /**
     * Return the datastore metadata.
     *
     * @return The datastore metadata.
     */
    DatastoreMetadata getDatastoreMetadata();

    /**
     * Return if the type is abstract.
     *
     * @return <code>true</code> if the type is abstract.
     */
    boolean isAbstract();

    /**
     * Return if the type is final.
     *
     * @return <code>true</code> if the type is final.
     */
    boolean isFinal();
}
