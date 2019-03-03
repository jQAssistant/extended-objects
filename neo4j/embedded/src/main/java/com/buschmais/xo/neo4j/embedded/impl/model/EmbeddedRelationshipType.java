package com.buschmais.xo.neo4j.embedded.impl.model;

import com.buschmais.xo.neo4j.api.model.Neo4jRelationshipType;

import org.neo4j.graphdb.RelationshipType;

public class EmbeddedRelationshipType implements Neo4jRelationshipType {

    private RelationshipType delegate;

    private String name;

    public EmbeddedRelationshipType(RelationshipType delegate) {
        this.delegate = delegate;
        this.name = delegate.name();
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

        EmbeddedRelationshipType that = (EmbeddedRelationshipType) o;

        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public RelationshipType getDelegate() {
        return delegate;
    }

    @Override
    public String toString() {
        return delegate.toString();
    }
}
