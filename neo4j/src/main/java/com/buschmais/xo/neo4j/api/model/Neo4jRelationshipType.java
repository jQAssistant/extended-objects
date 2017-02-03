package com.buschmais.xo.neo4j.api.model;

import org.neo4j.graphdb.RelationshipType;

public class Neo4jRelationshipType {

    private RelationshipType relationshipType;

    private String name;

    public Neo4jRelationshipType(RelationshipType relationshipType) {
        this.relationshipType = relationshipType;
        this.name = relationshipType.name();
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Neo4jRelationshipType that = (Neo4jRelationshipType) o;

        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public RelationshipType getRelationshipType() {
        return relationshipType;
    }

    @Override
    public String toString() {
        return relationshipType.toString();
    }
}
