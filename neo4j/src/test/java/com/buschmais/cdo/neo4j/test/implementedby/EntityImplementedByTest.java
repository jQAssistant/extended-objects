package com.buschmais.cdo.neo4j.test.implementedby;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.api.bootstrap.CdoUnit;
import com.buschmais.cdo.neo4j.test.AbstractCdoManagerTest;
import com.buschmais.cdo.neo4j.test.implementedby.composite.A;
import com.buschmais.cdo.neo4j.test.implementedby.composite.A2B;
import com.buschmais.cdo.neo4j.test.implementedby.composite.B;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.net.URISyntaxException;
import java.util.Collection;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class EntityImplementedByTest extends AbstractCdoManagerTest {

    public EntityImplementedByTest(CdoUnit cdoUnit) {
        super(cdoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getCdoUnits() throws URISyntaxException {
        return cdoUnits(A.class, B.class, A2B.class);
    }

    @Test
    public void nonPropertyMethod() {
        CdoManager cdoManager = getCdoManagerFactory().createCdoManager();
        cdoManager.currentTransaction().begin();
        A a = cdoManager.create(A.class);
        a.setValue(1);
        int i = a.incrementValue();
        assertThat(i, equalTo(2));
        cdoManager.currentTransaction().commit();
        cdoManager.close();
    }

    @Test
    public void propertyMethods() {
        CdoManager cdoManager = getCdoManagerFactory().createCdoManager();
        cdoManager.currentTransaction().begin();
        A a = cdoManager.create(A.class);
        a.setCustomValue("VALUE");
        String value = a.getCustomValue();
        assertThat(value, equalTo("set_VALUE_get"));
        cdoManager.currentTransaction().commit();
        cdoManager.close();
    }

    @Test
    public void compareTo() {
        CdoManager cdoManager = getCdoManagerFactory().createCdoManager();
        cdoManager.currentTransaction().begin();
        A a1 = cdoManager.create(A.class);
        a1.setValue(100);
        A a2 = cdoManager.create(A.class);
        a2.setValue(200);
        cdoManager.currentTransaction().commit();
        cdoManager.currentTransaction().begin();
        assertThat(a1.compareTo(a2), equalTo(-100));
        cdoManager.currentTransaction().commit();
        cdoManager.close();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void unsupportedOperation() {
        CdoManager cdoManager = getCdoManagerFactory().createCdoManager();
        cdoManager.currentTransaction().begin();
        A a = cdoManager.create(A.class);
        cdoManager.currentTransaction().commit();
        cdoManager.currentTransaction().begin();
        try {
            a.unsupportedOperation();
        } finally {
            cdoManager.currentTransaction().commit();
        }
    }
}
