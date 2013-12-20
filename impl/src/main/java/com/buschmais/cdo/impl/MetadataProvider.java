package com.buschmais.cdo.impl;

import com.buschmais.cdo.spi.datastore.DatastoreEntityMetadata;
import com.buschmais.cdo.spi.datastore.TypeMetadataSet;
import com.buschmais.cdo.spi.metadata.TypeMetadata;

import java.util.Collection;
import java.util.Set;

public interface MetadataProvider<EntityMetadata extends DatastoreEntityMetadata<Discriminator>, Discriminator> {

    TypeMetadataSet<EntityMetadata> getTypes(Set<Discriminator> discriminators);

    Set<Discriminator> getDiscriminators(TypeMetadataSet<EntityMetadata> types);

    Collection<TypeMetadata<EntityMetadata>> getRegisteredMetadata();

    TypeMetadata<EntityMetadata> getEntityMetadata(Class<?> type);
}
