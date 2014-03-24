package com.buschmais.xo.neo4j.test.relation.typed.composite;

import com.buschmais.xo.neo4j.api.annotation.Relation;

import static com.buschmais.xo.neo4j.api.annotation.Relation.Incoming;
import static com.buschmais.xo.neo4j.api.annotation.Relation.Outgoing;

@Relation
public interface TreeNodeRelation {

    int getVersion();

    void setVersion(int version);

    @Incoming
    TreeNode getChild();

    @Outgoing
    TreeNode getParent();
}
