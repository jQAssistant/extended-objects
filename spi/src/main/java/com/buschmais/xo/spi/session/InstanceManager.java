package com.buschmais.xo.spi.session;

/**
 * Created by Dirk Mahler on 29.10.2014.
 */
public interface InstanceManager<DatastoreId, DatastoreType> {
    <T> T readInstance(DatastoreType datastoreType);

    <T> T createInstance(DatastoreType datastoreType);

    <T> T updateInstance(DatastoreType datastoreType);

    <Instance> void removeInstance(Instance instance);

    <Instance> void closeInstance(Instance instance);

    <Instance> boolean isInstance(Instance instance);

    <Instance> DatastoreType getDatastoreType(Instance instance);

    /**
     * Return the unique id of a datastore type.
     *
     * @param datastoreType The datastore type.
     * @return The id.
     */
    DatastoreId getDatastoreId(DatastoreType datastoreType);

    void close();
}
