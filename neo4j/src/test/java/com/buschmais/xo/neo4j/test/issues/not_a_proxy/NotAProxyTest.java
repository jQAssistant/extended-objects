package com.buschmais.xo.neo4j.test.issues.not_a_proxy;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractCdoManagerTest;
import com.buschmais.xo.neo4j.test.issues.not_a_proxy.composite.A;
import com.buschmais.xo.neo4j.test.issues.not_a_proxy.composite.B;
import com.buschmais.xo.neo4j.test.issues.not_a_proxy.composite.C;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.net.URISyntaxException;
import java.util.Collection;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * https://github.com/buschmais/cdo-neo4j/issues/57
 */
@RunWith(Parameterized.class)
public class NotAProxyTest extends AbstractCdoManagerTest {

    public NotAProxyTest(XOUnit XOUnit) {
        super(XOUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getCdoUnits() throws URISyntaxException {
        return cdoUnits(A.class, B.class, C.class);
    }

    @Test
    public void test() {
        XOManager XOManager = getXOManager();
        XOManager.currentTransaction().begin();
        A a = XOManager.create(A.class);
        C c = XOManager.create(C.class);
        a.getB().add(c);
        XOManager.currentTransaction().commit();
        XOManager.currentTransaction().begin();
        assertThat(a.equals(a.getB()), is(false));
        XOManager.currentTransaction().commit();
    }

}
