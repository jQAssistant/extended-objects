package com.buschmais.xo.neo4j.test.implementedby;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractCdoManagerTest;
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
public class EntityImplementedByTest extends AbstractCdoManagerTest {

    public EntityImplementedByTest(XOUnit XOUnit) {
        super(XOUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getCdoUnits() throws URISyntaxException {
        return cdoUnits(A.class, B.class, A2B.class);
    }

    @Test
    public void nonPropertyMethod() {
        XOManager XOManager = getXOManagerFactory().createXOManager();
        XOManager.currentTransaction().begin();
        A a = XOManager.create(A.class);
        a.setValue(1);
        int i = a.incrementValue();
        assertThat(i, equalTo(2));
        XOManager.currentTransaction().commit();
        XOManager.close();
    }

    @Test
    public void propertyMethods() {
        XOManager XOManager = getXOManagerFactory().createXOManager();
        XOManager.currentTransaction().begin();
        A a = XOManager.create(A.class);
        a.setCustomValue("VALUE");
        String value = a.getCustomValue();
        assertThat(value, equalTo("set_VALUE_get"));
        XOManager.currentTransaction().commit();
        XOManager.close();
    }

    @Test
    public void compareTo() {
        XOManager XOManager = getXOManagerFactory().createXOManager();
        XOManager.currentTransaction().begin();
        A a1 = XOManager.create(A.class);
        a1.setValue(100);
        A a2 = XOManager.create(A.class);
        a2.setValue(200);
        XOManager.currentTransaction().commit();
        XOManager.currentTransaction().begin();
        assertThat(a1.compareTo(a2), equalTo(-100));
        XOManager.currentTransaction().commit();
        XOManager.close();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void unsupportedOperation() {
        XOManager XOManager = getXOManagerFactory().createXOManager();
        XOManager.currentTransaction().begin();
        A a = XOManager.create(A.class);
        XOManager.currentTransaction().commit();
        XOManager.currentTransaction().begin();
        try {
            a.unsupportedOperation();
        } finally {
            XOManager.currentTransaction().commit();
        }
    }
}
