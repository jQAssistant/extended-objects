package com.buschmais.cdo.spi.datastore;

import com.buschmais.cdo.spi.metadata.MetadataProvider;

/**
 * Defines the interface of a datastore.
 *
 * @param <DatastoreSession> The type of the sessions produced by the datastore.
 * @param <EntityMetadata>   The type of entity metadata used by the datastore.
 * @param <Discriminator>    The type of entity discriminators used by the datastore.
 */
public interface Datastore<DatastoreSession extends com.buschmais.cdo.spi.datastore.DatastoreSession, EntityMetadata extends DatastoreEntityMetadata<Discriminator>, Discriminator> {

    void init(MetadataProvider<EntityMetadata, Discriminator> metadataProvider);

    DatastoreMetadataFactory<EntityMetadata, Discriminator> getMetadataFactory();

    DatastoreSession createSession(MetadataProvider metadataProvider);

    void close();

}
