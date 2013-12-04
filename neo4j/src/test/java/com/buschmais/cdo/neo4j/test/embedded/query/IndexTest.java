package com.buschmais.cdo.neo4j.test.embedded.query;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.neo4j.test.embedded.AbstractEmbeddedCdoManagerTest;
import com.buschmais.cdo.neo4j.test.embedded.query.composite.A;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class IndexTest extends AbstractEmbeddedCdoManagerTest {

    @Override
    protected Class<?>[] getTypes() {
        return new Class<?>[]{A.class};
    }

    @Test
    public void index() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        A a1 = cdoManager.create(A.class);
        a1.setValue("A1");
        A a2_1 = cdoManager.create(A.class);
        a2_1.setValue("A2");
        A a2_2 = cdoManager.create(A.class);
        a2_2.setValue("A2");
        cdoManager.currentTransaction().commit();
        cdoManager.currentTransaction().begin();
        A a = cdoManager.find(A.class, "A1").getSingleResult();
        assertThat(a, equalTo(a1));
        try {
            cdoManager.find(A.class, "A2").getSingleResult();
            fail("Expecting a " + CdoException.class.getName());
        } catch (CdoException e) {

        }
        cdoManager.currentTransaction().commit();
    }
}
