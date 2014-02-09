package com.buschmais.cdo.impl.cache;

import java.util.Collection;

/**
 * Transactional cache whcih handles read and write access to instances.
 *
 * @param <Id> The datastore id type.
 */
public class TransactionalCache<Id> {

    /**
     * The access mode indicating how an instance has been accessed.
     */
    public enum Mode {
        READ,
        WRITE
    }

    /**
     * The read cache.
     */
    private final WeakReferenceCache<Id> readCache;

    /**
     * The write cache.
     */
    private final ReferenceCache<Id> writeCache;

    /**
     * Constructor.
     */
    public TransactionalCache() {
        this.readCache = new WeakReferenceCache<>();
        this.writeCache = new ReferenceCache<>();
    }

    /**
     * Put an instance into the cache.
     *
     * @param id    The id.
     * @param value The instance.
     * @param mode  The mode.
     */
    public void put(Id id, Object value, Mode mode) {
        if (Mode.WRITE.equals(mode)) {
            writeCache.put(id, value);
        }
        readCache.put(id, value);
    }

    /**
     * Lookup an instance in the cache identified by its id.
     *
     * @param id The id.
     * @return The corresponding instance or <code>null</code> if no instance is available.
     */
    public Object get(Id id) {
        Object value = writeCache.get(id);
        if (value == null) {
            value = readCache.get(id);
            if (value != null) {
                writeCache.put(id, value);
            }
        }
        return value;
    }

    /**
     * Removes an instance from the cache.
     *
     * @param id The id.
     */
    public void remove(Id id) {
        readCache.remove(id);
        writeCache.remove(id);
    }

    /**
     * Clear the cache.
     * <p>Note: Affects only the write.</p>
     */
    public void clear() {
        writeCache.clear();
    }

    /**
     * Returns the instance which have been read.
     *
     * @return The read instances.
     */
    public Collection<?> readInstances() {
        return readCache.values();
    }

    /**
     * Returns the instance which have been written.
     *
     * @return The written instances.
     */
    public Collection<?> writtenInstances() {
        return readCache.values();
    }
}
