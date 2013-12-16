package com.buschmais.cdo.spi.metadata;

import com.buschmais.cdo.spi.datastore.DatastoreEntityMetadata;
import com.buschmais.cdo.spi.datastore.TypeSet;

import java.util.Collection;
import java.util.Set;

public interface MetadataProvider<EntityMetadata extends DatastoreEntityMetadata<Discriminator>, Discriminator> {

    TypeSet getTypes(Set<Discriminator> discriminators);

    Collection<TypeMetadata<EntityMetadata>> getRegisteredMetadata();

    TypeMetadata<EntityMetadata> getEntityMetadata(Class<?> type);
}
