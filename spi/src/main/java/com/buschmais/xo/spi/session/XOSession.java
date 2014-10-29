package com.buschmais.xo.spi.session;

import com.buschmais.xo.api.ResultIterable;
import com.buschmais.xo.api.ResultIterator;
import com.buschmais.xo.spi.datastore.DatastoreEntityMetadata;
import com.buschmais.xo.spi.datastore.DatastoreRelationMetadata;
import com.buschmais.xo.spi.metadata.type.EntityTypeMetadata;
import com.buschmais.xo.spi.metadata.type.RelationTypeMetadata;
import com.buschmais.xo.spi.metadata.type.RepositoryTypeMetadata;
import com.buschmais.xo.spi.metadata.type.SimpleTypeMetadata;

/**
 * Defines functionality to be used by repository implementations.
 */
public interface XOSession<EntityId, Entity, EntityMetadata extends DatastoreEntityMetadata<EntityDiscriminator>, EntityDiscriminator, RelationId, Relation, RelationMetadata extends DatastoreRelationMetadata<RelationDiscriminator>, RelationDiscriminator, PropertyMetadata> {

    /**
     * Converts a value to the datastore representation.
     * 
     * @param value
     *            The value.
     * @param <T>
     *            The value type.
     * @param <D>
     *            The datastore type.
     * @return The datastore value.
     */
    <T, D> D toDatastore(T value);

    /**
     * Converts a value from the datastore representation.
     * 
     * @param value
     *            The value.
     * @param <T>
     *            The value type.
     * @param <D>
     *            The datastore type.
     * @return The value.
     */
    <D, T> T fromDatastore(D value);

    /**
     * Return the metadata for an entity type.
     * 
     * @param type
     *            The entity type.
     * @param <T>
     *            The entity type.
     * @return The metadata.
     */
    <T> EntityTypeMetadata<EntityMetadata> getEntityMetadata(Class<T> type);

    /**
     * Return the metadata for a relation type.
     * 
     * @param type
     *            The relation type.
     * @param <T>
     *            The relation type.
     * @return The metadata.
     */
    <T> RelationTypeMetadata<RelationMetadata> getRelationMetadata(Class<T> type);

    /**
     * Return the metadata for a repository type.
     *
     * @param type
     *            The repository type.
     * @param <R>
     *            The repository type.
     * @return The metadata.
     */
    <R> RepositoryTypeMetadata getRepositoryMetadata(Class<R> type);

    /**
     * Return the {@link com.buschmais.xo.spi.session.InstanceManager} for a
     * datastore entity or relation.
     * 
     * @param datastoreType
     *            The datastore type.
     * @param <D>
     *            The datastore type.
     * @return The instance manager or <code>null</code> if it is not a managed
     *         instance.
     */
    <D> InstanceManager<?, D> getInstanceManager(D datastoreType);

    /**
     * Converts a datastore result.
     * 
     * @param iterator
     *            The datastore result.
     * @param <D>
     *            The datastore type.
     * @param <T>
     *            The return type.
     * @return The converted result.
     */
    <D, T> ResultIterable<T> toResult(ResultIterator<D> iterator);

}
