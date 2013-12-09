package com.buschmais.cdo.neo4j.spi;

import java.util.Set;

public interface DatastoreMetadataProvider<Entity> {

    TypeSet getTypes(Entity entity);

}
