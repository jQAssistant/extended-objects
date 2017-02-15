package com.buschmais.xo.neo4j.test.relation.typed.composite;

import static com.buschmais.xo.neo4j.api.annotation.Relation.Incoming;
import static com.buschmais.xo.neo4j.api.annotation.Relation.Outgoing;

import java.util.List;

import com.buschmais.xo.neo4j.api.annotation.Label;

@Label
public interface TreeNode {

    void setName(String name);

    String getName();

    @Incoming
    TreeNodeRelation getParent();

    @Outgoing
    List<TreeNodeRelation> getChildren();

}
