package com.buschmais.xo.spi.bootstrap;

import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.spi.datastore.Datastore;
import com.buschmais.xo.spi.datastore.DatastoreEntityMetadata;
import com.buschmais.xo.spi.datastore.DatastoreRelationMetadata;

public interface XODatastoreProvider<EntityMetadata extends DatastoreEntityMetadata<EntityDiscriminator>, EntityDiscriminator, RelationMetadata extends DatastoreRelationMetadata<RelationDiscriminator>, RelationDiscriminator> {

    Datastore<?, EntityMetadata, EntityDiscriminator, RelationMetadata, RelationDiscriminator> createDatastore(XOUnit XOUnit);

}
