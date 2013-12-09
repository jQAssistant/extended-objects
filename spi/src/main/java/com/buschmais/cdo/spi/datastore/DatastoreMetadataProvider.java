package com.buschmais.cdo.spi.datastore;

public interface DatastoreMetadataProvider<Entity> {

    TypeSet getTypes(Entity entity);

}
