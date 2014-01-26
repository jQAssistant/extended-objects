package com.buschmais.cdo.neo4j.test.embedded.issues.not_a_proxy;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.neo4j.test.embedded.AbstractEmbeddedCdoManagerTest;
import com.buschmais.cdo.neo4j.test.embedded.issues.not_a_proxy.composite.A;
import com.buschmais.cdo.neo4j.test.embedded.issues.not_a_proxy.composite.B;
import com.buschmais.cdo.neo4j.test.embedded.issues.not_a_proxy.composite.C;

/**
 * https://github.com/buschmais/cdo-neo4j/issues/57
 */
public class NotAProxyTest extends AbstractEmbeddedCdoManagerTest {

    @Override
    protected Class<?>[] getTypes() {
        return new Class<?>[]{A.class, B.class, C.class};
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
