package com.buschmais.xo.neo4j.test.relation.typed.composite;

import com.buschmais.xo.neo4j.api.annotation.Relation;
import com.buschmais.xo.neo4j.api.annotation.Relation.From;
import com.buschmais.xo.neo4j.api.annotation.Relation.To;

@Relation
public interface TreeNodeRelation {

    int getVersion();

    void setVersion(int version);

    @To
    TreeNode getChild();

    @From
    TreeNode getParent();
}
