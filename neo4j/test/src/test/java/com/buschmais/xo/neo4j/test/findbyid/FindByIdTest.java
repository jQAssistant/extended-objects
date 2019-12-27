package com.buschmais.xo.neo4j.test.findbyid;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Collection;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractNeo4jXOManagerTest;
import com.buschmais.xo.neo4j.test.findbyid.composite.A;
import com.buschmais.xo.neo4j.test.findbyid.composite.A2B;
import com.buschmais.xo.neo4j.test.findbyid.composite.B;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class FindByIdTest extends AbstractNeo4jXOManagerTest {

    public FindByIdTest(XOUnit xoUnit) {
        super(xoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getXOUnits() {
        return xoUnits(A.class, A2B.class, B.class);
    }

    @Test
    public void entity() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction().begin();
        A a1 = xoManager.create(A.class);
        A a2 = xoManager.create(A.class);
        Long id1 = xoManager.getId(a1);
        Long id2 = xoManager.getId(a2);
        xoManager.currentTransaction().commit();
        xoManager.currentTransaction().begin();
        assertThat(xoManager.findById(A.class, id1), is(a1));
        assertThat(xoManager.findById(A.class, id2), is(a2));
        xoManager.currentTransaction().commit();
    }

    @Test
    public void relation() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction().begin();
        A a1 = xoManager.create(A.class);
        B b1 = xoManager.create(B.class);
        A2B a2b1 = xoManager.create(a1, A2B.class, b1);
        A a2 = xoManager.create(A.class);
        B b2 = xoManager.create(B.class);
        A2B a2b2 = xoManager.create(a2, A2B.class, b2);
        Long id1 = xoManager.getId(a2b1);
        Long id2 = xoManager.getId(a2b2);
        xoManager.currentTransaction().commit();
        xoManager.currentTransaction().begin();
        assertThat(xoManager.findById(A2B.class, id1), is(a2b1));
        assertThat(xoManager.findById(A2B.class, id2), is(a2b2));
        xoManager.currentTransaction().commit();
    }

}
