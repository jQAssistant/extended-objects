package com.buschmais.cdo.neo4j.test.invokeusing;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.neo4j.test.AbstractCdoManagerTest;
import com.buschmais.cdo.neo4j.test.invokeusing.composite.A;
import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class InvokeUsingTest extends AbstractCdoManagerTest {

    @Override
    protected Class<?>[] getTypes() {
        return new Class<?>[]{A.class};
    }

    @Test @Ignore
    public void downCast() {
        CdoManager cdoManager = getCdoManagerFactory().createCdoManager();
        cdoManager.begin();
        A a = cdoManager.create(A.class);
        a.setValue(1);
        int i = a.incrementValue();
        assertThat(i, equalTo(2));
        cdoManager.commit();
        cdoManager.close();
    }

}
