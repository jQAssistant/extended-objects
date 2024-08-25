package com.buschmais.xo.neo4j.test.issues.not_a_proxy;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractNeo4JXOManagerIT;
import com.buschmais.xo.neo4j.test.issues.not_a_proxy.composite.A;
import com.buschmais.xo.neo4j.test.issues.not_a_proxy.composite.B;
import com.buschmais.xo.neo4j.test.issues.not_a_proxy.composite.C;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * https://github.com/buschmais/cdo-neo4j/issues/57
 */
@RunWith(Parameterized.class)
public class NotAProxyIT extends AbstractNeo4JXOManagerIT {

    public NotAProxyIT(XOUnit xoUnit) {
        super(xoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getXOUnits() {
        return xoUnits(A.class, B.class, C.class);
    }

    @Test
    public void test() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction().begin();
        A a = xoManager.create(A.class);
        C c = xoManager.create(C.class);
        a.getB().add(c);
        xoManager.currentTransaction().commit();
        xoManager.currentTransaction().begin();
        assertThat(a).isNotEqualTo(a.getB());
        xoManager.currentTransaction().commit();
    }

}
