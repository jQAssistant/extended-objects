package com.buschmais.xo.neo4j.test.relation.filtered;

import java.util.Collection;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractNeo4JXOManagerIT;
import com.buschmais.xo.neo4j.test.relation.filtered.composite.A;
import com.buschmais.xo.neo4j.test.relation.filtered.composite.B;
import com.buschmais.xo.neo4j.test.relation.filtered.composite.C;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies filtering of entities referenced by relations of the same type.
 */
@RunWith(Parameterized.class)
public class FilteredEntityRelationIT extends AbstractNeo4JXOManagerIT {

    public FilteredEntityRelationIT(XOUnit xoUnit) {
        super(xoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getXOUnits() {
        return xoUnits(A.class, B.class, C.class);
    }

    /**
     * This behavior may be improved: there might be different to-one relations to
     * different types at the same time.
     */
    @Test
    public void oneToOne() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction()
            .begin();
        A a = xoManager.create(A.class);
        B b = xoManager.create(B.class);
        a.setB(b);
        xoManager.currentTransaction()
            .commit();
        xoManager.currentTransaction()
            .begin();
        assertThat(a.getB()).isEqualTo(b);
        assertThat(a.getC()).isNull();
        C c = xoManager.create(C.class);
        a.setC(c);
        xoManager.currentTransaction()
            .commit();
        xoManager.currentTransaction()
            .begin();
        assertThat(a.getB()).isNull();
        assertThat(a.getC()).isEqualTo(c);
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
        a.getListOfB()
            .add(b1);
        a.getListOfB()
            .add(b2);
        C c1 = xoManager.create(C.class);
        C c2 = xoManager.create(C.class);
        a.getListOfC()
            .add(c1);
        a.getListOfC()
            .add(c2);
        xoManager.currentTransaction()
            .commit();
        xoManager.currentTransaction()
            .begin();
        assertThat(a.getListOfB()).hasSize(2);
        assertThat(a.getListOfB()).contains(b1, b2);
        assertThat(a.getListOfC()).hasSize(2);
        assertThat(a.getListOfC()).contains(c1, c2);
        B b3 = xoManager.create(B.class);
        a.getListOfB()
            .add(b3);
        C c3 = xoManager.create(C.class);
        a.getListOfC()
            .add(c3);
        xoManager.currentTransaction()
            .commit();
        xoManager.currentTransaction()
            .begin();
        assertThat(a.getListOfB()).hasSize(3);
        assertThat(a.getListOfB()).contains(b1, b2, b3);
        assertThat(a.getListOfC()).hasSize(3);
        assertThat(a.getListOfC()).contains(c1, c2, c3);
        xoManager.currentTransaction()
            .commit();
    }
}
