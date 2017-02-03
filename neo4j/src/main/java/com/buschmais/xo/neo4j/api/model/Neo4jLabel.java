package com.buschmais.xo.neo4j.api.model;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Label;

public final class Neo4jLabel {

    private String name;

    private Label delegate;

    public Neo4jLabel(String name) {
        this(DynamicLabel.label(name));
    }

    public Neo4jLabel(Label delegate) {
        this.delegate = delegate;
        this.name = delegate.name();
    }

    public String getName() {
        return name;
    }

    public Label getDelegate() {
        return delegate;
    }

    @Override
    public String toString() {
        return delegate.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Neo4jLabel that = (Neo4jLabel) o;
        return name != null ? name.equals(that.name) : that.name == null;

    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
