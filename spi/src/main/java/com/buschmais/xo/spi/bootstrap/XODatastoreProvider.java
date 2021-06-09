package com.buschmais.xo.spi.bootstrap;

import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.spi.datastore.Datastore;
import com.buschmais.xo.api.metadata.type.DatastoreEntityMetadata;
import com.buschmais.xo.api.metadata.type.DatastoreRelationMetadata;

public interface XODatastoreProvider<EntityMetadata extends DatastoreEntityMetadata<EntityDiscriminator>, EntityDiscriminator, RelationMetadata extends DatastoreRelationMetadata<RelationDiscriminator>, RelationDiscriminator> {

    Datastore<?, EntityMetadata, EntityDiscriminator, RelationMetadata, RelationDiscriminator> createDatastore(XOUnit xoUnit);

    Class<? extends Enum<? extends ConfigurationProperty>> getConfigurationProperties();

    interface ConfigurationProperty {

        /**
         * Return the property key.
         *
         * @return The proeperty key.
         */
        String getKey();

        /**
         * Return the target type of the value.
         *
         * @return The target type.
         */
        Class<?> getType();

    }

}
