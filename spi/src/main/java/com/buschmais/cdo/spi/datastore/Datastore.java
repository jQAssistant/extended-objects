package com.buschmais.cdo.spi.datastore;

import com.buschmais.cdo.spi.metadata.TypeMetadata;
import com.buschmais.cdo.spi.metadata.MetadataProvider;

import java.util.Collection;

public interface Datastore<DS extends DatastoreSession> {

    DatastoreMetadataFactory<?> getMetadataFactory();

    DatastoreMetadataProvider createMetadataProvider(Collection<TypeMetadata> entityTypes);

    DS createSession(MetadataProvider metadataProvider);

    void close();

    void init(MetadataProvider metadataProvider);
}
