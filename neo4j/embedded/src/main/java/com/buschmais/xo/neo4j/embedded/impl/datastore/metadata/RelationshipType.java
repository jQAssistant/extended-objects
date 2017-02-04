package com.buschmais.xo.neo4j.embedded.impl.datastore.metadata;

public class RelationshipType implements org.neo4j.graphdb.RelationshipType {

    private final org.neo4j.graphdb.RelationshipType delegate;

    public RelationshipType(org.neo4j.graphdb.RelationshipType delegate) {
        this.delegate = delegate;
    }

    @Override
    public String name() {
        return delegate.name();
    }

    @Override
    public int hashCode() {
        return delegate.name().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof RelationshipType) {
            return delegate.name().equals(((RelationshipType) obj).delegate.name());
        }
        return false;
    }

    @Override
    public String toString() {
        return delegate.name();
    }
}
