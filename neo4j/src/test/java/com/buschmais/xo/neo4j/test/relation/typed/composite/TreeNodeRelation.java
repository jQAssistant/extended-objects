package com.buschmais.xo.neo4j.test.relation.typed.composite;

import com.buschmais.xo.neo4j.api.annotation.Relation;
import com.buschmais.xo.neo4j.api.annotation.Relation.Incoming;
import com.buschmais.xo.neo4j.api.annotation.Relation.Outgoing;

@Relation
public interface TreeNodeRelation {

    int getVersion();

    void setVersion(int version);

    @Incoming
    TreeNode getChild();

    @Outgoing
    TreeNode getParent();
}
