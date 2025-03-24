package com.buschmais.xo.neo4j.test.relation.typed;

import java.util.Collection;
import java.util.List;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractNeo4JXOManagerIT;
import com.buschmais.xo.neo4j.test.relation.typed.composite.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;

@RunWith(Parameterized.class)
public class TypedRelationIT extends AbstractNeo4JXOManagerIT {

    public TypedRelationIT(XOUnit xoUnit) {
        super(xoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getXOUnits() {
        return xoUnits(A.class, B.class, TypedOneToOneRelation.class, TypedOneToManyRelation.class, TypedManyToManyRelation.class);
    }

    @Test
    public void oneToOneWithLazyNodes() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction()
            .begin();
        List<TypedOneToOneRelation> relations = executeQuery("MERGE (:A)-[r:OneToOne{version:1}]->(:B) RETURN r").getColumn("r");
        TypedOneToOneRelation relation = assertThat(relations).hasSize(1)
            .first()
            .isNotNull()
            .isInstanceOf(TypedOneToOneRelation.class)
            .actual();
        assertThat(relation.getVersion()).isEqualTo(1);
        assertThat(relation.getA()).isNotNull()
            .isInstanceOf(A.class);
        assertThat(relation.getB()).isNotNull()
            .isInstanceOf(B.class);
        xoManager.currentTransaction()
            .commit();
    }

    @Test
    public void oneToOne() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction()
            .begin();
        A a = xoManager.create(A.class);
        B b1 = xoManager.create(B.class);
        B b2 = xoManager.create(B.class);
        TypedOneToOneRelation relation1 = xoManager.create(a, TypedOneToOneRelation.class, b1);
        relation1.setVersion(1);
        xoManager.currentTransaction()
            .commit();
        xoManager.currentTransaction()
            .begin();
        assertThat(a.getOneToOne()).isEqualTo(relation1);
        assertThat(relation1.getVersion()).isEqualTo(1);
        assertThat(relation1.getA()).isEqualTo(a);
        assertThat(relation1.getB()).isEqualTo(b1);
        assertThat(executeQuery("MATCH ()-[r]->() RETURN r").getColumn("r"), hasItem(equalTo(relation1)));
        TypedOneToOneRelation relation2 = xoManager.create(a, TypedOneToOneRelation.class, b2);
        xoManager.currentTransaction()
            .commit();
        xoManager.currentTransaction()
            .begin();
        assertThat(a.getOneToOne()).isEqualTo(relation2);
        assertThat(relation2.getA()).isEqualTo(a);
        assertThat(relation2.getB()).isEqualTo(b2);
        assertThat(executeQuery("MATCH ()-[r]->() RETURN r").getColumn("r"), hasItem(equalTo(relation2)));
        xoManager.delete(relation2);
        xoManager.currentTransaction()
            .commit();
        xoManager.currentTransaction()
            .begin();
        assertThat(a.getOneToOne()).isNull();
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
        TypedOneToManyRelation relationB1_1 = xoManager.create(a, TypedOneToManyRelation.class, b1);
        relationB1_1.setVersion(1);
        TypedOneToManyRelation relationB2_1 = xoManager.create(a, TypedOneToManyRelation.class, b2);
        relationB2_1.setVersion(2);
        xoManager.currentTransaction()
            .commit();
        xoManager.currentTransaction()
            .begin();
        assertThat(a.getOneToMany()).contains(relationB1_1, relationB2_1);
        assertThat(b1.getManyToOne()).isEqualTo(relationB1_1);
        assertThat(relationB1_1.getVersion()).isEqualTo(1);
        assertThat(relationB1_1.getA()).isEqualTo(a);
        assertThat(relationB1_1.getB()).isEqualTo(b1);
        assertThat(b2.getManyToOne()).isEqualTo(relationB2_1);
        assertThat(relationB2_1.getVersion()).isEqualTo(2);
        assertThat(relationB2_1.getA()).isEqualTo(a);
        assertThat(relationB2_1.getB()).isEqualTo(b2);
        assertThat(executeQuery("MATCH ()-[r]->() RETURN r").<TypedOneToManyRelation>getColumn("r")).contains(relationB1_1, relationB2_1);
        TypedOneToManyRelation relationB1_2 = xoManager.create(a, TypedOneToManyRelation.class, b1);
        relationB1_2.setVersion(3);
        TypedOneToManyRelation relationB2_2 = xoManager.create(a, TypedOneToManyRelation.class, b2);
        relationB2_2.setVersion(4);
        xoManager.currentTransaction()
            .commit();
        xoManager.currentTransaction()
            .begin();
        assertThat(a.getOneToMany()).contains(relationB1_2, relationB2_2);
        assertThat(b1.getManyToOne()).isEqualTo(relationB1_2);
        assertThat(relationB1_2.getVersion()).isEqualTo(3);
        assertThat(relationB1_2.getA()).isEqualTo(a);
        assertThat(relationB1_2.getB()).isEqualTo(b1);
        assertThat(b2.getManyToOne()).isEqualTo(relationB2_2);
        assertThat(relationB2_2.getVersion()).isEqualTo(4);
        assertThat(relationB2_2.getA()).isEqualTo(a);
        assertThat(relationB2_2.getB()).isEqualTo(b2);
        assertThat(executeQuery("MATCH ()-[r]->() RETURN r").<TypedOneToManyRelation>getColumn("r")).contains(relationB1_2, relationB2_2);
        xoManager.delete(relationB1_2);
        xoManager.delete(relationB2_2);
        xoManager.currentTransaction()
            .commit();
        xoManager.currentTransaction()
            .begin();
        assertThat(executeQuery("MATCH ()-[r]->() RETURN r").<TypedOneToManyRelation>getColumn("r")).isNull();
        xoManager.currentTransaction()
            .commit();
    }

    @Test
    public void manyToMany() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction()
            .begin();
        A a = xoManager.create(A.class);
        B b1 = xoManager.create(B.class);
        B b2 = xoManager.create(B.class);
        TypedManyToManyRelation relationB1_1 = xoManager.create(a, TypedManyToManyRelation.class, b1);
        relationB1_1.setVersion(1);
        TypedManyToManyRelation relationB1_2 = xoManager.create(a, TypedManyToManyRelation.class, b1);
        relationB1_2.setVersion(2);
        TypedManyToManyRelation relationB2_1 = xoManager.create(a, TypedManyToManyRelation.class, b2);
        relationB2_1.setVersion(3);
        TypedManyToManyRelation relationB2_2 = xoManager.create(a, TypedManyToManyRelation.class, b2);
        relationB2_2.setVersion(4);
        xoManager.currentTransaction()
            .commit();
        xoManager.currentTransaction()
            .begin();
        assertThat(a.getManyToMany()).contains(relationB1_1, relationB1_2, relationB2_1, relationB2_2);
        assertThat(b1.getManyToMany()).contains(relationB1_1, relationB1_2);
        assertThat(b2.getManyToMany()).contains(relationB2_1, relationB2_2);
        assertThat(relationB1_1.getVersion()).isEqualTo(1);
        assertThat(relationB1_1.getA()).isEqualTo(a);
        assertThat(relationB1_1.getB()).isEqualTo(b1);
        assertThat(relationB1_2.getVersion()).isEqualTo(2);
        assertThat(relationB1_2.getA()).isEqualTo(a);
        assertThat(relationB1_2.getB()).isEqualTo(b1);
        assertThat(relationB2_1.getVersion()).isEqualTo(3);
        assertThat(relationB2_1.getA()).isEqualTo(a);
        assertThat(relationB2_1.getB()).isEqualTo(b2);
        assertThat(relationB2_2.getVersion()).isEqualTo(4);
        assertThat(relationB2_2.getA()).isEqualTo(a);
        assertThat(relationB2_2.getB()).isEqualTo(b2);
        assertThat(executeQuery("MATCH ()-[r]->() RETURN r").<TypedManyToManyRelation>getColumn("r")).contains(relationB1_1, relationB1_2, relationB2_1,
            relationB2_2);
        xoManager.delete(relationB1_1);
        xoManager.delete(relationB2_1);
        xoManager.currentTransaction()
            .commit();
        xoManager.currentTransaction()
            .begin();
        assertThat(b1.getManyToMany()).contains(relationB1_2);
        assertThat(b2.getManyToMany()).contains(relationB2_2);
        assertThat(executeQuery("MATCH ()-[r]->() RETURN r").<TypedManyToManyRelation>getColumn("r")).contains(relationB1_2, relationB2_2);
        xoManager.currentTransaction()
            .commit();
    }
}
