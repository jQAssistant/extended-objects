package com.buschmais.cdo.neo4j.test.embedded.implementedby;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.neo4j.test.embedded.AbstractEmbeddedCdoManagerTest;
import com.buschmais.cdo.neo4j.test.embedded.implementedby.composite.A;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class ImplementedByTest extends AbstractEmbeddedCdoManagerTest {

    @Override
    protected Class<?>[] getTypes() {
        return new Class<?>[]{A.class};
    }

    @Test
    public void nonPropertyMethod() {
        CdoManager cdoManager = getCdoManagerFactory().createCdoManager();
        cdoManager.begin();
        A a = cdoManager.create(A.class);
        a.setValue(1);
        int i = a.incrementValue();
        assertThat(i, equalTo(2));
        cdoManager.commit();
        cdoManager.close();
    }

    @Test
    public void propertyMethods() {
        CdoManager cdoManager = getCdoManagerFactory().createCdoManager();
        cdoManager.begin();
        A a = cdoManager.create(A.class);
        a.setCustomValue("VALUE");
        String value = a.getCustomValue();
        assertThat(value, equalTo("set_VALUE_get"));
        cdoManager.commit();
        cdoManager.close();
    }

    @Test
    public void compareTo() {
        CdoManager cdoManager = getCdoManagerFactory().createCdoManager();
        cdoManager.begin();
        A a1 = cdoManager.create(A.class);
        a1.setValue(100);
        A a2 = cdoManager.create(A.class);
        a2.setValue(200);
        cdoManager.commit();
        cdoManager.begin();
        assertThat(a1.compareTo(a2), equalTo(-100));
        cdoManager.commit();
        cdoManager.close();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void unsupportedOperation() {
        CdoManager cdoManager = getCdoManagerFactory().createCdoManager();
        cdoManager.begin();
        A a = cdoManager.create(A.class);
        cdoManager.commit();
        cdoManager.begin();
        try {
            a.unsupportedOperation();
        } finally {
            cdoManager.commit();
        }
    }

}
