package com.buschmais.xo.impl.cache;

import java.util.Collection;

/**
 * Transactional cache whcih handles read and write access to instances.
 *
 * @param <Id>
 *            The datastore id type.
 */
public class TransactionalCache<Id> {

    private static class CacheKey<Id> {
        private Id id;

        private CacheKey(Id id) {
            this.id = id;
        }

        @Override
        public int hashCode() {
            return id.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return obj != null && id.equals(((CacheKey) obj).id);
        }
    }

    /**
     * The access mode indicating how an instance has been accessed.
     */
    public enum Mode {
        READ, WRITE
    }

    /**
     * The read cache.
     */
    private final WeakReferenceCache<CacheKey> readCache;

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
     * @param id
     *            The id.
     * @param value
     *            The instance.
     * @param mode
     *            The mode.
     */
    public void put(Id id, Object value, Mode mode) {
        if (Mode.WRITE.equals(mode)) {
            writeCache.put(id, value);
        }
        readCache.put(new CacheKey(id), value);
    }

    /**
     * Lookup an instance in the cache identified by its id.
     *
     * @param id
     *            The id.
     * @param mode
     *            The mode.
     * @return The corresponding instance or <code>null</code> if no instance is
     *         available.
     */
    public Object get(Id id, Mode mode) {
        Object value = writeCache.get(id);
        if (value == null) {
            value = readCache.get(new CacheKey(id));
            if (value != null && Mode.WRITE.equals(mode)) {
                writeCache.put(id, value);
            }
        }
        return value;
    }

    /**
     * Removes an instance from the cache.
     *
     * @param id
     *            The id.
     */
    public void remove(Id id) {
        readCache.remove(new CacheKey(id));
        writeCache.remove(id);
    }

    /**
     * Flush the cache, i.e. remove all entries which are written.
     */
    public void flush() {
        writeCache.clear();
    }

    /**
     * Clear the cache.
     */
    public void clear() {
        writeCache.clear();
        readCache.clear();
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
        return writeCache.values();
    }
}
