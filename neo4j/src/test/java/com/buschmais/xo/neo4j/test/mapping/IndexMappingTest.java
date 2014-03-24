package com.buschmais.xo.neo4j.test.mapping;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractCdoManagerTest;
import com.buschmais.xo.neo4j.test.mapping.composite.A;
import com.buschmais.xo.neo4j.test.mapping.composite.D;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.net.URISyntaxException;
import java.util.Collection;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class IndexMappingTest extends AbstractCdoManagerTest {

    public IndexMappingTest(XOUnit XOUnit) {
        super(XOUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getCdoUnits() throws URISyntaxException {
        return cdoUnits(A.class, D.class);
    }

    @Test
    public void indexedProperty() {
        XOManager XOManager = getXOManager();
        XOManager.currentTransaction().begin();
        A a1 = XOManager.create(A.class);
        a1.setIndex("1");
        A a2 = XOManager.create(A.class);
        a2.setIndex("2");
        XOManager.currentTransaction().commit();
        XOManager.currentTransaction().begin();
        assertThat(XOManager.find(A.class, "1").iterator().next(), equalTo(a1));
        assertThat(XOManager.find(A.class, "2").iterator().next(), equalTo(a2));
        assertThat(XOManager.find(A.class, "3").iterator().hasNext(), equalTo(false));
        XOManager.currentTransaction().commit();
    }

    @Test
    public void useIndexOf() {
        XOManager XOManager = getXOManager();
        XOManager.currentTransaction().begin();
        A a1 = XOManager.create(D.class);
        a1.setIndex("1");
        XOManager.currentTransaction().commit();
        XOManager.currentTransaction().begin();
        assertThat(XOManager.find(D.class, "1").iterator().next(), equalTo(a1));
        XOManager.currentTransaction().commit();
    }
}
