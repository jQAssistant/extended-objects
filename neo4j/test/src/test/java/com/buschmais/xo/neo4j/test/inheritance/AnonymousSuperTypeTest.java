package com.buschmais.xo.neo4j.test.inheritance;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.net.URISyntaxException;
import java.util.Collection;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractNeo4jXOManagerTest;
import com.buschmais.xo.neo4j.test.inheritance.composite.A;
import com.buschmais.xo.neo4j.test.inheritance.composite.B;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class AnonymousSuperTypeTest extends AbstractNeo4jXOManagerTest {

    public AnonymousSuperTypeTest(XOUnit xoUnit) {
        super(xoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getXOUnits() throws URISyntaxException {
        return xoUnits(A.class, B.class);
    }

    @Test
    public void anonymousSuperType() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction().begin();
        B b = xoManager.create(B.class);
        b.setIndex("1");
        b.setVersion(1);
        xoManager.currentTransaction().commit();
        xoManager.currentTransaction().begin();
        A a = xoManager.find(A.class, "1").iterator().next();
        assertThat(b, equalTo(a));
        assertThat(a.getVersion().longValue(), equalTo(1L));
        xoManager.currentTransaction().commit();
    }
}
