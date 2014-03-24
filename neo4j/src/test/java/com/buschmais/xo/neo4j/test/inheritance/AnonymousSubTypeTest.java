package com.buschmais.xo.neo4j.test.inheritance;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractCdoManagerTest;
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
public class AnonymousSubTypeTest extends AbstractCdoManagerTest {

    public AnonymousSubTypeTest(XOUnit XOUnit) {
        super(XOUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getCdoUnits() throws URISyntaxException {
        return cdoUnits(D.class);
    }

    @Test
    public void anonymousSubType() {
        XOManager XOManager = getXOManager();
        XOManager.currentTransaction().begin();
        D b = XOManager.create(D.class);
        b.setIndex("1");
        XOManager.currentTransaction().commit();
        closeCdoManager();
        XOManager = getXOManager();
        XOManager.currentTransaction().begin();
        A a = XOManager.find(A.class, "1").iterator().next();
        assertThat(a.getIndex(), equalTo("1"));
        XOManager.currentTransaction().commit();
    }

}
