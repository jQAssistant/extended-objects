package com.buschmais.xo.neo4j.test.inheritance;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractCdoManagerTest;
import com.buschmais.xo.neo4j.test.inheritance.composite.A;
import com.buschmais.xo.neo4j.test.inheritance.composite.B;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.net.URISyntaxException;
import java.util.Collection;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class AnonymousSuperTypeTest extends AbstractCdoManagerTest {

    public AnonymousSuperTypeTest(XOUnit XOUnit) {
        super(XOUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getCdoUnits() throws URISyntaxException {
        return cdoUnits(A.class, B.class);
    }

    @Test
    public void anonymousSuperType() {
        XOManager XOManager = getXOManager();
        XOManager.currentTransaction().begin();
        B b = XOManager.create(B.class);
        b.setIndex("1");
        b.setVersion(1);
        XOManager.currentTransaction().commit();
        XOManager.currentTransaction().begin();
        A a = XOManager.find(A.class, "1").iterator().next();
        assertThat(b, equalTo(a));
        assertThat(a.getVersion().longValue(), equalTo(1L));
        XOManager.currentTransaction().commit();
    }
}
