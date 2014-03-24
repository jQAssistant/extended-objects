package com.buschmais.xo.neo4j.test.inheritance;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractXOManagerTest;
import com.buschmais.xo.neo4j.test.inheritance.composite.A;
import com.buschmais.xo.neo4j.test.inheritance.composite.D;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.net.URISyntaxException;
import java.util.Collection;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class AnonymousSubTypeTest extends AbstractXOManagerTest {

    public AnonymousSubTypeTest(XOUnit xoUnit) {
        super(xoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getXOUnits() throws URISyntaxException {
        return xoUnits(D.class);
    }

    @Test
    public void anonymousSubType() {
        XOManager xoManager = getXoManager();
        xoManager.currentTransaction().begin();
        D b = xoManager.create(D.class);
        b.setIndex("1");
        xoManager.currentTransaction().commit();
        closeXOmanager();
        xoManager = getXoManager();
        xoManager.currentTransaction().begin();
        A a = xoManager.find(A.class, "1").iterator().next();
        assertThat(a.getIndex(), equalTo("1"));
        xoManager.currentTransaction().commit();
    }

}
