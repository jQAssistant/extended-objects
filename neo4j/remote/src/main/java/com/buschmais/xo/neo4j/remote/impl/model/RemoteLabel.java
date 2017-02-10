package com.buschmais.xo.neo4j.remote.impl.model;

import com.buschmais.xo.neo4j.api.model.Neo4jLabel;

public class RemoteLabel implements Neo4jLabel {

    private String name;

    public RemoteLabel(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        RemoteLabel that = (RemoteLabel) o;

        return name != null ? name.equals(that.name) : that.name == null;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "RemoteLabel{" + "name='" + name + '\'' + '}';
    }
}
