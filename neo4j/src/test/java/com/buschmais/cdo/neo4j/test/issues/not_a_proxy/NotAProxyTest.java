package com.buschmais.cdo.neo4j.test.issues.not_a_proxy;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.api.bootstrap.CdoUnit;
import com.buschmais.cdo.neo4j.test.AbstractCdoManagerTest;
import com.buschmais.cdo.neo4j.test.issues.not_a_proxy.composite.A;
import com.buschmais.cdo.neo4j.test.issues.not_a_proxy.composite.B;
import com.buschmais.cdo.neo4j.test.issues.not_a_proxy.composite.C;
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

    public NotAProxyTest(CdoUnit cdoUnit) {
        super(cdoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getCdoUnits() throws URISyntaxException {
        return cdoUnits(A.class, B.class, C.class);
    }

    @Test
    public void test() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        A a = cdoManager.create(A.class);
        C c = cdoManager.create(C.class);
        a.getB().add(c);
        cdoManager.currentTransaction().commit();
        cdoManager.currentTransaction().begin();
        assertThat(a.equals(a.getB()), is(false));
        cdoManager.currentTransaction().commit();
    }

}
