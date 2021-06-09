package com.buschmais.xo.neo4j.remote.impl.model.state;

import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@ToString
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

    public boolean isLoaded() {
        return readCache != null;
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

    public void afterCompletion(boolean clear) {
        if (clear) {
            readCache = null;
            writeCache = null;
        }
    }
}
