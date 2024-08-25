package com.buschmais.xo.neo4j.test.relation.qualified;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractNeo4JXOManagerIT;
import com.buschmais.xo.neo4j.test.relation.qualified.composite.A;
import com.buschmais.xo.neo4j.test.relation.qualified.composite.B;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class QualifiedRelationIT extends AbstractNeo4JXOManagerIT {

    public QualifiedRelationIT(XOUnit xoUnit) {
        super(xoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getXOUnits() {
        return xoUnits(A.class, B.class);
    }

    @Test
    public void oneToOne() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction()
            .begin();
        A a = xoManager.create(A.class);
        B b1 = xoManager.create(B.class);
        a.setOneToOne(b1);
        xoManager.currentTransaction()
            .commit();
        xoManager.currentTransaction()
            .begin();
        assertThat(a.getOneToOne()).isEqualTo(b1);
        assertThat(b1.getOneToOne()).isEqualTo(a);
        assertThat(executeQuery("MATCH (a:A)-[:OneToOne]->(b:B) RETURN b").getColumn("b")).contains(b1);
        B b2 = xoManager.create(B.class);
        a.setOneToOne(b2);
        xoManager.currentTransaction()
            .commit();
        xoManager.currentTransaction()
            .begin();
        assertThat(a.getOneToOne()).isEqualTo(b2);
        assertThat(b2.getOneToOne()).isEqualTo(a);
        assertThat(b1.getOneToOne()).isNull();
        assertThat(executeQuery("MATCH (a:A)-[:OneToOne]->(b:B) RETURN b").getColumn("b")).contains(b2);
        a.setOneToOne(null);
        xoManager.currentTransaction()
            .commit();
        xoManager.currentTransaction()
            .begin();
        assertThat(a.getOneToOne()).isNull();
        assertThat(b1.getOneToOne()).isNull();
        assertThat(b2.getOneToOne()).isNull();
        xoManager.currentTransaction()
            .commit();
    }

    @Test
    public void oneToMany() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction()
            .begin();
        A a = xoManager.create(A.class);
        B b1 = xoManager.create(B.class);
        B b2 = xoManager.create(B.class);
        a.getOneToMany()
            .add(b1);
        a.getOneToMany()
            .add(b2);
        xoManager.currentTransaction()
            .commit();
        xoManager.currentTransaction()
            .begin();
        assertThat(a.getOneToMany()).contains(b1, b2);
        assertThat(b1.getManyToOne()).isEqualTo(a);
        assertThat(b2.getManyToOne()).isEqualTo(a);
        assertThat(executeQuery("MATCH (a:A)-[:OneToMany]->(b:B) RETURN b").<B>getColumn("b")).contains(b1, b2);
        a.getOneToMany()
            .remove(b1);
        a.getOneToMany()
            .remove(b2);
        B b3 = xoManager.create(B.class);
        B b4 = xoManager.create(B.class);
        a.getOneToMany()
            .add(b3);
        a.getOneToMany()
            .add(b4);
        xoManager.currentTransaction()
            .commit();
        xoManager.currentTransaction()
            .begin();
        assertThat(a.getOneToMany()).contains(b3, b4);
        assertThat(b1.getManyToOne()).isNull();
        assertThat(b2.getManyToOne()).isNull();
        assertThat(b3.getManyToOne()).isEqualTo(a);
        assertThat(b4.getManyToOne()).isEqualTo(a);
        assertThat(executeQuery("MATCH (a:A)-[:OneToMany]->(b:B) RETURN b").<B>getColumn("b")).contains(b3, b4);
        xoManager.currentTransaction()
            .commit();
    }

    @Test
    public void manyToMany() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction()
            .begin();
        A a1 = xoManager.create(A.class);
        A a2 = xoManager.create(A.class);
        B b1 = xoManager.create(B.class);
        B b2 = xoManager.create(B.class);
        a1.getManyToMany()
            .add(b1);
        a1.getManyToMany()
            .add(b2);
        a2.getManyToMany()
            .add(b1);
        a2.getManyToMany()
            .add(b2);
        xoManager.currentTransaction()
            .commit();
        xoManager.currentTransaction()
            .begin();
        assertThat(a1.getManyToMany()).contains(b1, b2);
        assertThat(a2.getManyToMany()).contains(b1, b2);
        assertThat(b1.getManyToMany()).contains(a1, a2);
        assertThat(b2.getManyToMany()).contains(a1, a2);
        assertThat(executeQuery("MATCH (a:A)-[:ManyToMany]->(b:B) RETURN a, collect(b) as listOfB ORDER BY ID(a)").<A>getColumn("a")).contains(a1, a2);
        assertThat(
            executeQuery("MATCH (a:A)-[:ManyToMany]->(b:B) RETURN a, collect(b) as listOfB ORDER BY ID(a)").<Iterable<B>>getColumn("listOfB")).containsExactly(
            List.of(b1, b2), List.of(b1, b2));
        a1.getManyToMany()
            .remove(b1);
        a2.getManyToMany()
            .remove(b1);
        xoManager.currentTransaction()
            .commit();
        xoManager.currentTransaction()
            .begin();
        assertThat(a1.getManyToMany()).contains(b2);
        assertThat(a2.getManyToMany()).contains(b2);
        assertThat(b1.getManyToMany()).isEmpty();
        assertThat(b2.getManyToMany()).contains(a1, a2);
        xoManager.currentTransaction()
            .commit();
    }

    @Test
    public void oneToOneNewDeleted() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction()
            .begin();
        A a = xoManager.create(A.class);
        B b1 = xoManager.create(B.class);
        a.setOneToOne(b1);
        assertThat(executeQuery("MATCH (a:A)-[:OneToOne]->(b:B) RETURN b").getColumn("b")).contains(b1);
        B b2 = xoManager.create(B.class);
        B b3 = xoManager.create(B.class);
        a.setOneToOne(b2);
        a.setOneToOne(b3);
        a.setOneToOne(null);
        assertThat(a.getOneToOne()).isNull();
        xoManager.currentTransaction()
            .commit();
        xoManager.currentTransaction()
            .begin();
        assertThat(a.getOneToOne()).isNull();
        xoManager.currentTransaction()
            .commit();
    }

    @Test
    public void updateManyToOne() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction()
            .begin();
        A a = xoManager.create(A.class);
        List<B> bList = new ArrayList<>();
        long details = 1000;
        for (int i = 0; i < details; i++) {
            B b = xoManager.create(B.class);
            bList.add(b);
        }
        xoManager.currentTransaction()
            .commit();

        xoManager.currentTransaction()
            .begin();
        for (B b : bList) {
            b.setManyToOne(a);
        }
        xoManager.currentTransaction()
            .commit();

        xoManager.currentTransaction()
            .begin();
        List<Long> count = executeQuery("MATCH (a:A)-[:OneToMany]->(b:B) RETURN count(b) as count").getColumn("count");
        assertThat(count.get(0)).isEqualTo(details);
        List<B> bs = executeQuery("MATCH (b:B) RETURN b").getColumn("b");
        for (B b : bs) {
            b.setManyToOne(null);
        }
        xoManager.currentTransaction()
            .commit();
        xoManager.currentTransaction()
            .begin();
        assertThat(a.getOneToMany()).isEmpty();
        xoManager.currentTransaction()
            .commit();
    }

    @Test
    public void updateManyToOneWithinSameTransaction() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction()
            .begin();
        A a = xoManager.create(A.class);
        B b = xoManager.create(B.class);
        b.setManyToOne(a);
        xoManager.flush();
        b.setManyToOne(null);
        xoManager.currentTransaction()
            .commit();

        xoManager.currentTransaction()
            .begin();
        assertThat(a.getOneToMany()).isEmpty();
        xoManager.currentTransaction()
            .commit();
    }
}
