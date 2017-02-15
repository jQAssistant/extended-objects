package com.buschmais.xo.neo4j.test.issues.not_a_proxy;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.net.URISyntaxException;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractNeo4jXOManagerTest;
import com.buschmais.xo.neo4j.test.issues.not_a_proxy.composite.A;
import com.buschmais.xo.neo4j.test.issues.not_a_proxy.composite.B;
import com.buschmais.xo.neo4j.test.issues.not_a_proxy.composite.C;

/**
 * https://github.com/buschmais/cdo-neo4j/issues/57
 */
@RunWith(Parameterized.class)
public class NotAProxyTest extends AbstractNeo4jXOManagerTest {

    public NotAProxyTest(XOUnit xoUnit) {
        super(xoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getXOUnits() throws URISyntaxException {
        return xoUnits(A.class, B.class, C.class);
    }

    @Test
    public void test() {
        XOManager xoManager = getXoManager();
        xoManager.currentTransaction().begin();
        A a = xoManager.create(A.class);
        C c = xoManager.create(C.class);
        a.getB().add(c);
        xoManager.currentTransaction().commit();
        xoManager.currentTransaction().begin();
        assertThat(a.equals(a.getB()), is(false));
        xoManager.currentTransaction().commit();
    }

}
