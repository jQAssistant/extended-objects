package com.buschmais.cdo.spi.bootstrap;

import com.buschmais.cdo.api.bootstrap.CdoUnit;
import com.buschmais.cdo.spi.datastore.Datastore;
import com.buschmais.cdo.spi.datastore.DatastoreEntityMetadata;
import com.buschmais.cdo.spi.datastore.DatastoreRelationMetadata;

public interface CdoDatastoreProvider<EntityMetadata extends DatastoreEntityMetadata<EntityDiscriminator>, EntityDiscriminator, RelationMetadata extends DatastoreRelationMetadata<RelationDiscriminator>, RelationDiscriminator> {

    Datastore<?, EntityMetadata, EntityDiscriminator, RelationMetadata, RelationDiscriminator> createDatastore(CdoUnit cdoUnit);

}
