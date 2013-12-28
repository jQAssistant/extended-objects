package com.buschmais.cdo.spi.datastore;

/**
 * Defines the interface for relation metadata produced and used by the datastore.
 *
 * @param <RelationDiscriminator> The type of relation discriminators.
 */
public interface DatastoreRelationMetadata<RelationDiscriminator> {

    /**
     * Return the discriminator value for the relation.
     *
     * @return The discriminator value for the relation.
     */
    RelationDiscriminator getDiscriminator();

}
