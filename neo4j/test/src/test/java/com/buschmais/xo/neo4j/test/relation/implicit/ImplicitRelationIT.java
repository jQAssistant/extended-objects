package com.buschmais.xo.neo4j.test.relation.implicit;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractNeo4JXOManagerIT;
import com.buschmais.xo.neo4j.test.relation.implicit.composite.A;
import com.buschmais.xo.neo4j.test.relation.implicit.composite.B;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class ImplicitRelationIT extends AbstractNeo4JXOManagerIT {

    public ImplicitRelationIT(XOUnit xoUnit) {
        super(xoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getXOUnits() {
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
        assertThat(a.getOneToOne()).isEqualTo(b1);
        assertThat(b1.getOneToOne()).isEqualTo(a);
        assertThat(executeQuery("MATCH (a:A)-[:IMPLICIT_ONE_TO_ONE]->(b:B) RETURN b").getColumn("b")).contains(b1);
        B b2 = xoManager.create(B.class);
        a.setOneToOne(b2);
        xoManager.currentTransaction().commit();
        xoManager.currentTransaction().begin();
        assertThat(a.getOneToOne()).isEqualTo(b2);
        assertThat(b2.getOneToOne()).isEqualTo(a);
        assertThat(b1.getOneToOne()).isNull();
        assertThat(executeQuery("MATCH (a:A)-[:IMPLICIT_ONE_TO_ONE]->(b:B) RETURN b").getColumn("b")).contains(b2);
        a.setOneToOne(null);
        xoManager.currentTransaction().commit();
        xoManager.currentTransaction().begin();
        assertThat(a.getOneToOne()).isNull();
        assertThat(b1.getOneToOne()).isNull();
        assertThat(b2.getOneToOne()).isNull();
        xoManager.currentTransaction().commit();
    }
}
