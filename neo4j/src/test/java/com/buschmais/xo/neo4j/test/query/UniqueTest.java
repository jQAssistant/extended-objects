package com.buschmais.xo.neo4j.test.query;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractCdoManagerTest;
import com.buschmais.xo.neo4j.test.query.composite.B;
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

    public UniqueTest(XOUnit XOUnit) {
        super(XOUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getCdoUnits() throws URISyntaxException {
        return cdoUnits(asList(Database.MEMORY), asList(B.class));
    }

    @Test(expected = ConstraintViolationException.class)
    public void denyDuplicates() {
        XOManager XOManager = getXOManager();
        XOManager.currentTransaction().begin();
        B a1 = XOManager.create(B.class);
        a1.setValue("A1");
        B a2_1 = XOManager.create(B.class);
        a2_1.setValue("A2");
        B a2_2 = XOManager.create(B.class);
        a2_2.setValue("A2");
        XOManager.currentTransaction().commit();
    }

    @Test
    public void index() {
        XOManager XOManager = getXOManager();
        XOManager.currentTransaction().begin();
        B a1 = XOManager.create(B.class);
        a1.setValue("A1");
        B a2_1 = XOManager.create(B.class);
        a2_1.setValue("A2");
        XOManager.currentTransaction().commit();
        XOManager.currentTransaction().begin();
        B a = XOManager.find(B.class, "A1").getSingleResult();
        assertThat(a, equalTo(a1));
        try {
            XOManager.find(B.class, "A3").getSingleResult();
            fail("Expecting a " + XOException.class.getName());
        } catch (XOException e) {

        }
        XOManager.currentTransaction().commit();
    }
}
