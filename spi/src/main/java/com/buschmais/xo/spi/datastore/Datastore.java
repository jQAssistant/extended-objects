package com.buschmais.xo.spi.datastore;

import java.util.Map;

import com.buschmais.xo.api.metadata.type.DatastoreEntityMetadata;
import com.buschmais.xo.api.metadata.type.DatastoreRelationMetadata;
import com.buschmais.xo.api.metadata.type.TypeMetadata;

/**
 * Defines the interface of a datastore.
 *
 * @param <DatastoreSession>
 *            The type of the sessions produced by the datastore.
 * @param <EntityMetadata>
 *            The type of entity metadata used by the datastore.
 * @param <EntityDiscriminator>
 *            The type of entity discriminators used by the datastore.
 * @param <RelationMetadata>
 *            The type of relation metadata used by the datastore.
 * @param <RelationDiscriminator>
 *            The type of relation discriminators used by the datastore.
 */
public interface Datastore<DatastoreSession extends com.buschmais.xo.spi.datastore.DatastoreSession, EntityMetadata extends DatastoreEntityMetadata<EntityDiscriminator>, EntityDiscriminator, RelationMetadata extends DatastoreRelationMetadata<RelationDiscriminator>, RelationDiscriminator>
        extends AutoCloseable {

    /**
     * Initialize the datastore.
     *
     * @param registeredMetadata
     *            A map of all registered types and their associated metadata.
     */
    void init(Map<Class<?>, TypeMetadata> registeredMetadata);

    /**
     * Return the datastore specific metadata factory.
     *
     * @return The metadata factory.
     */
    DatastoreMetadataFactory<EntityMetadata, EntityDiscriminator, RelationMetadata, RelationDiscriminator> getMetadataFactory();

    /**
     * Create a datastore session, e.g. open a connection to the datastore.
     *
     * @return The session.
     */
    DatastoreSession createSession();

    /**
     * Close the datastore.
     */
    void close();

}
