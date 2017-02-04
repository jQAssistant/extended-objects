package com.buschmais.xo.neo4j.embedded.impl.model;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Label;

import com.buschmais.xo.neo4j.api.model.Neo4jLabel;

public final class EmbeddedLabel implements Neo4jLabel {

    private String name;

    private Label delegate;

    public EmbeddedLabel(String name) {
        this(DynamicLabel.label(name));
    }

    public EmbeddedLabel(Label delegate) {
        this.delegate = delegate;
        this.name = delegate.name();
    }

    @Override
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
        EmbeddedLabel that = (EmbeddedLabel) o;
        return name != null ? name.equals(that.name) : that.name == null;

    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
