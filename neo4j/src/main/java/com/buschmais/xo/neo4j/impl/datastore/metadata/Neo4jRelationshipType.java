package com.buschmais.xo.neo4j.impl.datastore.metadata;

import org.neo4j.graphdb.RelationshipType;

public class Neo4jRelationshipType implements RelationshipType {

    private final RelationshipType delegate;

    public Neo4jRelationshipType(RelationshipType delegate) {
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
            return delegate.name().equals(((RelationshipType) obj).name());
        }
        return false;
    }

    @Override
    public String toString() {
        return delegate.name();
    }
}
