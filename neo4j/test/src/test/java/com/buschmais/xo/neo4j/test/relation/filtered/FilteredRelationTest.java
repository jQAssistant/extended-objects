package com.buschmais.xo.neo4j.test.relation.filtered;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.junit.Assert.assertThat;

import java.util.Collection;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractNeo4jXOManagerTest;
import com.buschmais.xo.neo4j.test.relation.filtered.composite.A;
import com.buschmais.xo.neo4j.test.relation.filtered.composite.B;
import com.buschmais.xo.neo4j.test.relation.filtered.composite.C;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class FilteredRelationTest extends AbstractNeo4jXOManagerTest {

    public FilteredRelationTest(XOUnit xoUnit) {
        super(xoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getXOUnits() {
        return xoUnits(A.class, B.class, C.class);
    }

    @Test
    public void oneToMany() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction().begin();
        A a = xoManager.create(A.class);
        B b1 = xoManager.create(B.class);
        B b2 = xoManager.create(B.class);
        a.getB().add(b1);
        a.getB().add(b2);
        C c1 = xoManager.create(C.class);
        C c2 = xoManager.create(C.class);
        a.getC().add(c1);
        a.getC().add(c2);
        xoManager.currentTransaction().commit();
        xoManager.currentTransaction().begin();
        assertThat(a.getB().size(), equalTo(2));
        assertThat(a.getB(), hasItems(b1, b2));
        assertThat(a.getC().size(), equalTo(2));
        assertThat(a.getC(), hasItems(c1, c2));
        B b3 = xoManager.create(B.class);
        a.getB().add(b3);
        C c3 = xoManager.create(C.class);
        a.getC().add(c3);
        xoManager.currentTransaction().commit();
        xoManager.currentTransaction().begin();
        assertThat(a.getB().size(), equalTo(3));
        assertThat(a.getB(), hasItems(b1, b2, b3));
        assertThat(a.getC().size(), equalTo(3));
        assertThat(a.getC(), hasItems(c1, c2, c3));
        xoManager.currentTransaction().commit();
    }
}
