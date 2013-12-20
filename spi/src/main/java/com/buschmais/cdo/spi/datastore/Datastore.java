package com.buschmais.cdo.spi.datastore;

import com.buschmais.cdo.spi.metadata.TypeMetadata;

import java.util.Collection;

/**
 * Defines the interface of a datastore.
 *
 * @param <DatastoreSession> The type of the sessions produced by the datastore.
 * @param <EntityMetadata>   The type of entity metadata used by the datastore.
 * @param <Discriminator>    The type of entity discriminators used by the datastore.
 */
public interface Datastore<DatastoreSession extends com.buschmais.cdo.spi.datastore.DatastoreSession, EntityMetadata extends DatastoreEntityMetadata<Discriminator>, Discriminator> {

    /**
     * Initialize the datastore.
     *
     * @param registeredMetadata A collection of all registerted types.
     */
    void init(Collection<TypeMetadata<EntityMetadata>> registeredMetadata);

    /**
     * Return the datastore specific metadata factory.
     *
     * @return The metadata factory.
     */
    DatastoreMetadataFactory<EntityMetadata, Discriminator> getMetadataFactory();

    /**
     * Create a datastore session, e.g. open a connection to the datastore.
     *
     * @return The session.
     */
    DatastoreSession createSession();

    /**
     * Close the datastore.
     */
    void close();

}
