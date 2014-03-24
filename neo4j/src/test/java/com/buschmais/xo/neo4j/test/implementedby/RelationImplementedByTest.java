package com.buschmais.xo.neo4j.test.implementedby;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractXOManagerTest;
import com.buschmais.xo.neo4j.test.implementedby.composite.A;
import com.buschmais.xo.neo4j.test.implementedby.composite.A2B;
import com.buschmais.xo.neo4j.test.implementedby.composite.B;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.net.URISyntaxException;
import java.util.Collection;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class RelationImplementedByTest extends AbstractXOManagerTest {

    public RelationImplementedByTest(XOUnit xoUnit) {
        super(xoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getXOUnits() throws URISyntaxException {
        return xoUnits(A.class, B.class, A2B.class);
    }

    @Test
    public void nonPropertyMethod() {
        XOManager xoManager = getXoManagerFactory().createXOManager();
        xoManager.currentTransaction().begin();
        A a = xoManager.create(A.class);
        B b = xoManager.create(B.class);
        A2B a2b = xoManager.create(a, A2B.class, b);
        a2b.setValue(1);
        int i = a2b.incrementValue();
        assertThat(i, equalTo(2));
        xoManager.currentTransaction().commit();
        xoManager.close();
    }

    @Test
    public void propertyMethods() {
        XOManager xoManager = getXoManagerFactory().createXOManager();
        xoManager.currentTransaction().begin();
        A a = xoManager.create(A.class);
        B b = xoManager.create(B.class);
        A2B a2b = xoManager.create(a, A2B.class, b);
        a2b.setCustomValue("VALUE");
        String value = a2b.getCustomValue();
        assertThat(value, equalTo("set_VALUE_get"));
        xoManager.currentTransaction().commit();
        xoManager.close();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void unsupportedOperation() {
        XOManager xoManager = getXoManagerFactory().createXOManager();
        xoManager.currentTransaction().begin();
        A a = xoManager.create(A.class);
        B b = xoManager.create(B.class);
        A2B a2b = xoManager.create(a, A2B.class, b);
        xoManager.currentTransaction().commit();
        xoManager.currentTransaction().begin();
        try {
            a2b.unsupportedOperation();
        } finally {
            xoManager.currentTransaction().commit();
        }
    }
}
