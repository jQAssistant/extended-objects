package com.buschmais.xo.neo4j.test.relation.typed;

import java.util.Collection;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractNeo4JXOManagerIT;
import com.buschmais.xo.neo4j.test.relation.typed.composite.TreeNode;
import com.buschmais.xo.neo4j.test.relation.typed.composite.TreeNodeRelation;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class TreeIT extends AbstractNeo4JXOManagerIT {

    public TreeIT(XOUnit xoUnit) {
        super(xoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getXOUnits() {
        return xoUnits(TreeNode.class, TreeNodeRelation.class);
    }

    @Test
    public void tree() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction()
            .begin();
        TreeNode parent = xoManager.create(TreeNode.class);
        parent.setName("parent");
        TreeNode child1 = xoManager.create(TreeNode.class);
        child1.setName("child 1");
        TreeNode child2 = xoManager.create(TreeNode.class);
        child2.setName("child 2");
        TreeNodeRelation relation1 = xoManager.create(parent, TreeNodeRelation.class, child1);
        relation1.setVersion(1);
        TreeNodeRelation relation2 = xoManager.create(parent, TreeNodeRelation.class, child2);
        relation2.setVersion(1);
        xoManager.currentTransaction()
            .commit();
        xoManager.currentTransaction()
            .begin();
        assertThat(parent.getParent()).isNull();
        assertThat(parent.getChildren()).contains(relation1, relation2);
        assertThat(relation1.getParent()).isEqualTo(parent);
        assertThat(relation1.getChild()).isEqualTo(child1);
        assertThat(relation2.getParent()).isEqualTo(parent);
        assertThat(relation2.getChild()).isEqualTo(child2);
        assertThat(child1.getParent()).isEqualTo(relation1);
        assertThat(child1.getChildren()).isEmpty();
        assertThat(child2.getParent()).isEqualTo(relation2);
        assertThat(child2.getChildren()).isEmpty();
    }
}
