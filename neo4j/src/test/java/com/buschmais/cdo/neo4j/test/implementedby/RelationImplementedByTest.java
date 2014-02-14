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
public class RelationImplementedByTest extends AbstractCdoManagerTest {

    public RelationImplementedByTest(CdoUnit cdoUnit) {
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
        B b = cdoManager.create(B.class);
        A2B a2b = cdoManager.create(a, A2B.class, b);
        a2b.setValue(1);
        int i = a2b.incrementValue();
        assertThat(i, equalTo(2));
        cdoManager.currentTransaction().commit();
        cdoManager.close();
    }

    @Test
    public void propertyMethods() {
        CdoManager cdoManager = getCdoManagerFactory().createCdoManager();
        cdoManager.currentTransaction().begin();
        A a = cdoManager.create(A.class);
        B b = cdoManager.create(B.class);
        A2B a2b = cdoManager.create(a, A2B.class, b);
        a2b.setCustomValue("VALUE");
        String value = a2b.getCustomValue();
        assertThat(value, equalTo("set_VALUE_get"));
        cdoManager.currentTransaction().commit();
        cdoManager.close();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void unsupportedOperation() {
        CdoManager cdoManager = getCdoManagerFactory().createCdoManager();
        cdoManager.currentTransaction().begin();
        A a = cdoManager.create(A.class);
        B b = cdoManager.create(B.class);
        A2B a2b = cdoManager.create(a, A2B.class, b);
        cdoManager.currentTransaction().commit();
        cdoManager.currentTransaction().begin();
        try {
            a2b.unsupportedOperation();
        } finally {
            cdoManager.currentTransaction().commit();
        }
    }
}
