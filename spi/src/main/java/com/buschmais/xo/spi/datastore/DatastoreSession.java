package com.buschmais.xo.spi.datastore;

import java.lang.annotation.Annotation;

import com.buschmais.xo.spi.session.XOSession;

/**
 * Defines the interface of a datastore session, e.g. a connection to the
 * datastore.
 *
 * @param <EntityId>
 *            The type of entity ids used by the datastore.
 * @param <Entity>
 *            The type of entities used by the datastore.
 * @param <EntityDiscriminator>
 *            The type of entity discriminators used by the datastore.
 * @param <RelationId>
 *            The type of relation ids used by the datastore.
 * @param <Relation>
 *            The type of relations used by the datastore.
 */
public interface DatastoreSession<EntityId, Entity, EntityMetadata extends DatastoreEntityMetadata<EntityDiscriminator>, EntityDiscriminator, RelationId, Relation, RelationMetadata extends DatastoreRelationMetadata<RelationDiscriminator>, RelationDiscriminator, PropertyMetadata>
        extends AutoCloseable {

    /**
     * Return the instance of the datastore transaction associated with the session.
     *
     * @return The datastore transaction.
     */
    DatastoreTransaction getDatastoreTransaction();

    /**
     * Return the {@link com.buschmais.xo.spi.datastore.DatastoreEntityManager}.
     *
     * @return The {@link com.buschmais.xo.spi.datastore.DatastoreEntityManager} .
     */
    DatastoreEntityManager<EntityId, Entity, EntityMetadata, EntityDiscriminator, PropertyMetadata> getDatastoreEntityManager();

    /**
     * Return the {@link com.buschmais.xo.spi.datastore.DatastoreRelationManager}.
     *
     * @return The {@link com.buschmais.xo.spi.datastore.DatastoreRelationManager}.
     */
    DatastoreRelationManager<Entity, RelationId, Relation, RelationMetadata, RelationDiscriminator, PropertyMetadata> getDatastoreRelationManager();

    /**
     * Return the default query language supported by this datastore.
     *
     * @return The default query language.
     */
    Class<? extends Annotation> getDefaultQueryLanguage();

    /**
     * Create a query for the given query language.
     *
     * @param queryLanguage
     *            The query language.
     * @param <QL>
     *            The query language type.
     * @return The query.
     */
    <QL extends Annotation> DatastoreQuery<QL> createQuery(Class<QL> queryLanguage);

    /**
     * Create a datastore repository for the given type.
     * 
     * @param xoSession
     *            The {@link com.buschmais.xo.spi.session.XOSession}.
     * @param type
     * @return The repository type.
     */
    <R> R createRepository(XOSession xoSession, Class<R> type);

    /**
     * Close the session.
     */
    void close();

}
