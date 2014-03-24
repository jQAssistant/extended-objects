package com.buschmais.xo.neo4j.test.query;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractCdoManagerTest;
import com.buschmais.xo.neo4j.test.query.composite.A;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.net.URISyntaxException;
import java.util.Collection;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class IndexTest extends AbstractCdoManagerTest {

    public IndexTest(XOUnit XOUnit) {
        super(XOUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getCdoUnits() throws URISyntaxException {
        return cdoUnits(A.class);
    }

    @Test
    public void index() {
        XOManager XOManager = getXOManager();
        XOManager.currentTransaction().begin();
        A a1 = XOManager.create(A.class);
        a1.setValue("A1");
        A a2_1 = XOManager.create(A.class);
        a2_1.setValue("A2");
        A a2_2 = XOManager.create(A.class);
        a2_2.setValue("A2");
        XOManager.currentTransaction().commit();
        XOManager.currentTransaction().begin();
        A a = XOManager.find(A.class, "A1").getSingleResult();
        assertThat(a, equalTo(a1));
        try {
            XOManager.find(A.class, "A2").getSingleResult();
            fail("Expecting a " + XOException.class.getName());
        } catch (XOException e) {

        }
        XOManager.currentTransaction().commit();
    }
}
