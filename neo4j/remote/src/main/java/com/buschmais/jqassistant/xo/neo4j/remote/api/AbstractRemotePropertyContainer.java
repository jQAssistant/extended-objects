package com.buschmais.jqassistant.xo.neo4j.remote.api;

import java.util.Map;

import com.buschmais.xo.neo4j.api.model.Neo4jPropertyContainer;

public class AbstractRemotePropertyContainer implements Neo4jPropertyContainer {

    @Override
    public boolean hasProperty(String key) {
        return false;
    }

    @Override
    public Object getProperty(String key) {
        return null;
    }

    @Override
    public Map<String, Object> getProperties() {
        return null;
    }
}

