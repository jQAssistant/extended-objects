package com.buschmais.xo.neo4j.test.relation.qualified;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractNeo4jXOManagerTest;
import com.buschmais.xo.neo4j.test.relation.qualified.composite.A;
import com.buschmais.xo.neo4j.test.relation.qualified.composite.B;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class QualifiedRelationTest extends AbstractNeo4jXOManagerTest {

    public QualifiedRelationTest(XOUnit xoUnit) {
        super(xoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getXOUnits() throws URISyntaxException {
        return xoUnits(A.class, B.class);
    }

    @Test
    public void oneToOne() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction().begin();
        A a = xoManager.create(A.class);
        B b1 = xoManager.create(B.class);
        a.setOneToOne(b1);
        xoManager.currentTransaction().commit();
        xoManager.currentTransaction().begin();
        assertThat(a.getOneToOne(), equalTo(b1));
        assertThat(b1.getOneToOne(), equalTo(a));
        assertThat(executeQuery("MATCH (a:A)-[:OneToOne]->(b:B) RETURN b").getColumn("b"), hasItem(b1));
        B b2 = xoManager.create(B.class);
        a.setOneToOne(b2);
        xoManager.currentTransaction().commit();
        xoManager.currentTransaction().begin();
        assertThat(a.getOneToOne(), equalTo(b2));
        assertThat(b2.getOneToOne(), equalTo(a));
        assertThat(b1.getOneToOne(), equalTo(null));
        assertThat(executeQuery("MATCH (a:A)-[:OneToOne]->(b:B) RETURN b").getColumn("b"), hasItem(b2));
        a.setOneToOne(null);
        xoManager.currentTransaction().commit();
        xoManager.currentTransaction().begin();
        assertThat(a.getOneToOne(), equalTo(null));
        assertThat(b1.getOneToOne(), equalTo(null));
        assertThat(b2.getOneToOne(), equalTo(null));
        xoManager.currentTransaction().commit();
    }

    @Test
    public void oneToMany() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction().begin();
        A a = xoManager.create(A.class);
        B b1 = xoManager.create(B.class);
        B b2 = xoManager.create(B.class);
        a.getOneToMany().add(b1);
        a.getOneToMany().add(b2);
        xoManager.currentTransaction().commit();
        xoManager.currentTransaction().begin();
        assertThat(a.getOneToMany(), hasItems(b1, b2));
        assertThat(b1.getManyToOne(), equalTo(a));
        assertThat(b2.getManyToOne(), equalTo(a));
        assertThat(executeQuery("MATCH (a:A)-[:OneToMany]->(b:B) RETURN b").<B>getColumn("b"), hasItems(b1, b2));
        a.getOneToMany().remove(b1);
        a.getOneToMany().remove(b2);
        B b3 = xoManager.create(B.class);
        B b4 = xoManager.create(B.class);
        a.getOneToMany().add(b3);
        a.getOneToMany().add(b4);
        xoManager.currentTransaction().commit();
        xoManager.currentTransaction().begin();
        assertThat(a.getOneToMany(), hasItems(b3, b4));
        assertThat(b1.getManyToOne(), equalTo(null));
        assertThat(b2.getManyToOne(), equalTo(null));
        assertThat(b3.getManyToOne(), equalTo(a));
        assertThat(b4.getManyToOne(), equalTo(a));
        assertThat(executeQuery("MATCH (a:A)-[:OneToMany]->(b:B) RETURN b").<B>getColumn("b"), hasItems(b3, b4));
        xoManager.currentTransaction().commit();
    }

    @Test
    public void manyToMany() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction().begin();
        A a1 = xoManager.create(A.class);
        A a2 = xoManager.create(A.class);
        B b1 = xoManager.create(B.class);
        B b2 = xoManager.create(B.class);
        a1.getManyToMany().add(b1);
        a1.getManyToMany().add(b2);
        a2.getManyToMany().add(b1);
        a2.getManyToMany().add(b2);
        xoManager.currentTransaction().commit();
        xoManager.currentTransaction().begin();
        assertThat(a1.getManyToMany(), hasItems(b1, b2));
        assertThat(a2.getManyToMany(), hasItems(b1, b2));
        assertThat(b1.getManyToMany(), hasItems(a1, a2));
        assertThat(b2.getManyToMany(), hasItems(a1, a2));
        assertThat(executeQuery("MATCH (a:A)-[:ManyToMany]->(b:B) RETURN a, collect(b) as listOfB ORDER BY ID(a)").<A>getColumn("a"), hasItems(a1, a2));
        assertThat(executeQuery("MATCH (a:A)-[:ManyToMany]->(b:B) RETURN a, collect(b) as listOfB ORDER BY ID(a)").<Iterable<B>>getColumn("listOfB"),
            hasItems(hasItems(b1, b2), hasItems(b1, b2)));
        a1.getManyToMany().remove(b1);
        a2.getManyToMany().remove(b1);
        xoManager.currentTransaction().commit();
        xoManager.currentTransaction().begin();
        assertThat(a1.getManyToMany(), hasItems(b2));
        assertThat(a2.getManyToMany(), hasItems(b2));
        assertThat(b1.getManyToMany().isEmpty(), equalTo(true));
        assertThat(b2.getManyToMany(), hasItems(a1, a2));
        xoManager.currentTransaction().commit();
    }

    @Test
    public void oneToOneNewDeleted() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction().begin();
        A a = xoManager.create(A.class);
        B b1 = xoManager.create(B.class);
        a.setOneToOne(b1);
        assertThat(executeQuery("MATCH (a:A)-[:OneToOne]->(b:B) RETURN b").getColumn("b"), hasItem(b1));
        B b2 = xoManager.create(B.class);
        B b3 = xoManager.create(B.class);
        a.setOneToOne(b2);
        a.setOneToOne(b3);
        a.setOneToOne(null);
        assertThat(a.getOneToOne(), equalTo(null));
        xoManager.currentTransaction().commit();
        xoManager.currentTransaction().begin();
        assertThat(a.getOneToOne(), equalTo(null));
        xoManager.currentTransaction().commit();
    }

    @Test
    public void oneToManyWithManyRelations() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction().begin();
        A a = xoManager.create(A.class);
        List<B> bList = new ArrayList<>();
        long details = 1000;
        for (int i = 0; i < details; i++) {
            B b = xoManager.create(B.class);
            bList.add(b);
        }
        xoManager.currentTransaction().commit();

//        xoManager.clear();

        xoManager.currentTransaction().begin();
        for (B b : bList) {
            b.setManyToOne(a);
        }
        xoManager.currentTransaction().commit();

//        xoManager.clear();

        xoManager.currentTransaction().begin();
        List<Long> count = executeQuery("MATCH (a:A)-[:OneToMany]->(b:B) RETURN count(b) as count").getColumn("count");
        assertThat(count.get(0), equalTo(details));
        List<B> bs = executeQuery("MATCH (b:B) RETURN b").getColumn("b");
        for (B b : bs) {
//            assertThat(b.getManyToOne(), equalTo(a));
            b.setManyToOne(null);
        }
        xoManager.currentTransaction().commit();
        xoManager.currentTransaction().begin();
        assertThat(a.getOneToMany().isEmpty(), equalTo(true));
        xoManager.currentTransaction().commit();
    }

}
