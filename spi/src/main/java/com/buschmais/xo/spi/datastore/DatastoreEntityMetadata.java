package com.buschmais.xo.spi.datastore;

/**
 * Defines the interface for entity metadata produced and used by the datastore.
 *
 * @param <EntityDiscriminator>
 *            The type of entity discriminators.
 */
public interface DatastoreEntityMetadata<EntityDiscriminator> {

    /**
     * Return the discriminator value for the entity.
     *
     * @return The discriminator value for the entity.
     */
    EntityDiscriminator getDiscriminator();

}
