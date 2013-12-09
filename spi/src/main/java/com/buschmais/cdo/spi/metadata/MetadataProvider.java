package com.buschmais.cdo.spi.metadata;

import com.buschmais.cdo.spi.datastore.DatastoreMetadataProvider;
import com.buschmais.cdo.spi.metadata.TypeMetadata;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: Dirk Mahler
 * Date: 09.12.13
 * Time: 23:01
 * To change this template use File | Settings | File Templates.
 */
public interface MetadataProvider {
    DatastoreMetadataProvider getDatastoreMetadataProvider();

    Collection<TypeMetadata> getRegisteredMetadata();

    TypeMetadata getEntityMetadata(Class<?> type);
}
