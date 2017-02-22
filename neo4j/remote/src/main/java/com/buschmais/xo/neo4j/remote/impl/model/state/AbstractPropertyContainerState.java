package com.buschmais.xo.neo4j.remote.impl.model.state;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractPropertyContainerState {

    private Map<String, Object> readCache;

    private Map<String, Object> writeCache = null;

    protected AbstractPropertyContainerState(Map<String, Object> readCache) {
        this.readCache = readCache;
    }

    public void load(Map<String, Object> properties) {
        readCache = new HashMap<>(properties);
    }

    public Map<String, Object> getReadCache() {
        return readCache;
    }

    public Map<String, Object> getWriteCache() {
        return writeCache;
    }

    public Map<String, Object> getOrCreateWriteCache() {
        if (writeCache == null) {
            writeCache = new HashMap<>();
        }
        return writeCache;
    }

    public void flush() {
        writeCache = null;
    }

    public void clear() {
        readCache = null;
        writeCache = null;
    }
}
