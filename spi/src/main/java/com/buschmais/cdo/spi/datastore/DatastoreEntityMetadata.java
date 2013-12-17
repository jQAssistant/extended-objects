package com.buschmais.cdo.spi.datastore;

/**
 * Defines the interface for entity metadata produced and used by the datastore.
 *
 * @param <Discriminator> The type of entity discriminators.
 */
public interface DatastoreEntityMetadata<Discriminator> {

    /**
     * Return the discriminator value for the entity.
     *
     * @return The discriminator value for the entity.
     */
    Discriminator getDiscriminator();

}
