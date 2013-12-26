package com.buschmais.cdo.neo4j.test.embedded.query;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.neo4j.test.embedded.AbstractEmbeddedCdoManagerTest;
import com.buschmais.cdo.neo4j.test.embedded.query.composite.B;
import org.junit.Test;
import org.neo4j.graphdb.ConstraintViolationException;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class UniqueTest extends AbstractEmbeddedCdoManagerTest {

    @Override
    protected Class<?>[] getTypes() {
        return new Class<?>[]{B.class};
    }

    @Test(expected = ConstraintViolationException.class)
    public void denyDuplicates() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        B a1 = cdoManager.create(B.class);
        a1.setValue("A1");
        B a2_1 = cdoManager.create(B.class);
        a2_1.setValue("A2");
        B a2_2 = cdoManager.create(B.class);
        a2_2.setValue("A2");
        cdoManager.currentTransaction().commit();
    }

    @Test
    public void index() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        B a1 = cdoManager.create(B.class);
        a1.setValue("A1");
        B a2_1 = cdoManager.create(B.class);
        a2_1.setValue("A2");
        cdoManager.currentTransaction().commit();
        cdoManager.currentTransaction().begin();
        B a = cdoManager.find(B.class, "A1").getSingleResult();
        assertThat(a, equalTo(a1));
        try {
            cdoManager.find(B.class, "A3").getSingleResult();
            fail("Expecting a " + CdoException.class.getName());
        } catch (CdoException e) {

        }
        cdoManager.currentTransaction().commit();
    }
}
