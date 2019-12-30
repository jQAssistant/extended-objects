package com.buschmais.xo.neo4j.remote.impl.model;

import com.buschmais.xo.neo4j.api.model.Neo4jRelationshipType;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class RemoteRelationshipType implements Neo4jRelationshipType {

    private final String name;

}
