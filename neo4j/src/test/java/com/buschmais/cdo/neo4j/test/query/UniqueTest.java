package com.buschmais.cdo.neo4j.test.query;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.api.bootstrap.CdoUnit;
import com.buschmais.cdo.neo4j.test.AbstractCdoManagerTest;
import com.buschmais.cdo.neo4j.test.query.composite.B;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.neo4j.graphdb.ConstraintViolationException;

import java.net.URISyntaxException;
import java.util.Collection;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class UniqueTest extends AbstractCdoManagerTest {

    public UniqueTest(CdoUnit cdoUnit) {
        super(cdoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getCdoUnits() throws URISyntaxException {
        return cdoUnits(asList(Database.MEMORY), asList(B.class));
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
