package com.buschmais.xo.neo4j.test.query;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.net.URISyntaxException;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.neo4j.graphdb.ConstraintViolationException;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractNeo4jXOManagerTest;
import com.buschmais.xo.neo4j.test.query.composite.B;

@RunWith(Parameterized.class)
public class UniqueTest extends AbstractNeo4jXOManagerTest {

    public UniqueTest(XOUnit xoUnit) {
        super(xoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getXOUnits() throws URISyntaxException {
        return xoUnits(asList(Neo4jDatabase.MEMORY), asList(B.class));
    }

    @Test(expected = ConstraintViolationException.class)
    public void denyDuplicates() {
        XOManager xoManager = getXoManager();
        xoManager.currentTransaction().begin();
        B a1 = xoManager.create(B.class);
        a1.setValue("A1");
        B a2_1 = xoManager.create(B.class);
        a2_1.setValue("A2");
        B a2_2 = xoManager.create(B.class);
        a2_2.setValue("A2");
        xoManager.currentTransaction().commit();
    }

    @Test
    public void index() {
        XOManager xoManager = getXoManager();
        xoManager.currentTransaction().begin();
        B a1 = xoManager.create(B.class);
        a1.setValue("A1");
        B a2_1 = xoManager.create(B.class);
        a2_1.setValue("A2");
        xoManager.currentTransaction().commit();
        xoManager.currentTransaction().begin();
        B a = xoManager.find(B.class, "A1").getSingleResult();
        assertThat(a, equalTo(a1));
        try {
            xoManager.find(B.class, "A3").getSingleResult();
            fail("Expecting a " + XOException.class.getName());
        } catch (XOException e) {

        }
        xoManager.currentTransaction().commit();
    }
}
