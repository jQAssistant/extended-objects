package com.buschmais.cdo.neo4j.test.embedded.relation.typed;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.neo4j.test.embedded.AbstractEmbeddedCdoManagerTest;
import com.buschmais.cdo.neo4j.test.embedded.relation.typed.composite.TreeNode;
import com.buschmais.cdo.neo4j.test.embedded.relation.typed.composite.TreeNodeRelation;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.junit.Assert.assertThat;

/**
 * Created by dimahler on 2/13/14.
 */
public class TreeTest extends AbstractEmbeddedCdoManagerTest {

    @Override
    protected Class<?>[] getTypes() {
        return new Class<?>[]{TreeNode.class, TreeNodeRelation.class};
    }

    @Test
    public void tree() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        TreeNode parent = cdoManager.create(TreeNode.class);
        parent.setName("parent");
        TreeNode child1 = cdoManager.create(TreeNode.class);
        child1.setName("child 1");
        TreeNode child2 = cdoManager.create(TreeNode.class);
        child2.setName("child 2");
        TreeNodeRelation relation1 = cdoManager.create(parent, TreeNodeRelation.class, child1);
        relation1.setVersion(1);
        TreeNodeRelation relation2 = cdoManager.create(parent, TreeNodeRelation.class, child2);
        relation2.setVersion(1);
        cdoManager.currentTransaction().commit();
        cdoManager.currentTransaction().begin();
        assertThat(parent.getParent(), equalTo(null));
        assertThat(parent.getChildren(), hasItems(relation1, relation2));
        assertThat(relation1.getParent(), equalTo(parent));
        assertThat(relation1.getChild(), equalTo(child1));
        assertThat(relation2.getParent(), equalTo(parent));
        assertThat(relation2.getChild(), equalTo(child2));
        assertThat(child1.getParent(), equalTo(relation1));
        assertThat(child1.getChildren().isEmpty(), equalTo(true));
        assertThat(child2.getParent(), equalTo(relation2));
        assertThat(child2.getChildren().isEmpty(), equalTo(true));
    }
}
