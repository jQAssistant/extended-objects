package com.buschmais.cdo.neo4j.test.implementedby;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.neo4j.test.AbstractCdoManagerTest;
import com.buschmais.cdo.neo4j.test.implementedby.composite.A;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class ImplementedByTest extends AbstractCdoManagerTest {

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
}
