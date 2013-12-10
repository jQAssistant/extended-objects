package com.buschmais.cdo.spi.metadata;

import com.buschmais.cdo.spi.datastore.DatastoreMetadataProvider;
import com.buschmais.cdo.spi.metadata.TypeMetadata;

import java.util.Collection;

public interface MetadataProvider {

    DatastoreMetadataProvider getDatastoreMetadataProvider();

    Collection<TypeMetadata> getRegisteredMetadata();

    TypeMetadata getEntityMetadata(Class<?> type);
}
