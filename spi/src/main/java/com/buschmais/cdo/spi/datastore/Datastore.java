package com.buschmais.cdo.spi.datastore;

import com.buschmais.cdo.spi.metadata.MetadataProvider;

public interface Datastore<DS extends DatastoreSession, EntityMetadata extends DatastoreEntityMetadata<Discriminator> , Discriminator> {

    DatastoreMetadataFactory<EntityMetadata, Discriminator> getMetadataFactory();

    DS createSession(MetadataProvider metadataProvider);

    void close();

    void init(MetadataProvider<EntityMetadata, Discriminator> metadataProvider);
}
