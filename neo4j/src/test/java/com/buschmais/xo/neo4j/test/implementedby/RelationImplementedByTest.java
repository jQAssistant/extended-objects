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
public class RelationImplementedByTest extends AbstractCdoManagerTest {

    public RelationImplementedByTest(XOUnit XOUnit) {
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
        B b = XOManager.create(B.class);
        A2B a2b = XOManager.create(a, A2B.class, b);
        a2b.setValue(1);
        int i = a2b.incrementValue();
        assertThat(i, equalTo(2));
        XOManager.currentTransaction().commit();
        XOManager.close();
    }

    @Test
    public void propertyMethods() {
        XOManager XOManager = getXOManagerFactory().createXOManager();
        XOManager.currentTransaction().begin();
        A a = XOManager.create(A.class);
        B b = XOManager.create(B.class);
        A2B a2b = XOManager.create(a, A2B.class, b);
        a2b.setCustomValue("VALUE");
        String value = a2b.getCustomValue();
        assertThat(value, equalTo("set_VALUE_get"));
        XOManager.currentTransaction().commit();
        XOManager.close();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void unsupportedOperation() {
        XOManager XOManager = getXOManagerFactory().createXOManager();
        XOManager.currentTransaction().begin();
        A a = XOManager.create(A.class);
        B b = XOManager.create(B.class);
        A2B a2b = XOManager.create(a, A2B.class, b);
        XOManager.currentTransaction().commit();
        XOManager.currentTransaction().begin();
        try {
            a2b.unsupportedOperation();
        } finally {
            XOManager.currentTransaction().commit();
        }
    }
}
