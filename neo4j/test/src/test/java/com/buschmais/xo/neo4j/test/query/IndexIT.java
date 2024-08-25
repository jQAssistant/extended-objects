package com.buschmais.xo.neo4j.test.query;

import java.util.Collection;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractNeo4JXOManagerIT;
import com.buschmais.xo.neo4j.test.query.composite.A;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class IndexIT extends AbstractNeo4JXOManagerIT {

    public IndexIT(XOUnit xoUnit) {
        super(xoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getXOUnits() {
        return xoUnits(A.class);
    }

    @Test
    public void index() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction()
            .begin();
        A a1 = xoManager.create(A.class);
        a1.setValue("A1");
        A a2_1 = xoManager.create(A.class);
        a2_1.setValue("A2");
        A a2_2 = xoManager.create(A.class);
        a2_2.setValue("A2");
        xoManager.currentTransaction()
            .commit();
        xoManager.currentTransaction()
            .begin();
        A a = xoManager.find(A.class, "A1")
            .getSingleResult();
        assertThat(a).isEqualTo(a1);
        try {
            xoManager.find(A.class, "A2")
                .getSingleResult();
            fail("Expecting a " + XOException.class.getName());
        } catch (XOException e) {

        }
        xoManager.currentTransaction()
            .commit();
    }
}
