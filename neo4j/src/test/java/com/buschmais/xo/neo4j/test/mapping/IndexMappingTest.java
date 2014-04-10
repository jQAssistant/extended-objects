package com.buschmais.xo.neo4j.test.mapping;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractNeo4jXOManagerTest;
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
public class IndexMappingTest extends AbstractNeo4jXOManagerTest {

    public IndexMappingTest(XOUnit xoUnit) {
        super(xoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getXOUnits() throws URISyntaxException {
        return xoUnits(A.class, D.class);
    }

    @Test
    public void indexedProperty() {
        XOManager xoManager = getXoManager();
        xoManager.currentTransaction().begin();
        A a1 = xoManager.create(A.class);
        a1.setIndex("1");
        A a2 = xoManager.create(A.class);
        a2.setIndex("2");
        xoManager.currentTransaction().commit();
        xoManager.currentTransaction().begin();
        assertThat(xoManager.find(A.class, "1").iterator().next(), equalTo(a1));
        assertThat(xoManager.find(A.class, "2").iterator().next(), equalTo(a2));
        assertThat(xoManager.find(A.class, "3").iterator().hasNext(), equalTo(false));
        xoManager.currentTransaction().commit();
    }

    @Test
    public void useIndexOf() {
        XOManager xoManager = getXoManager();
        xoManager.currentTransaction().begin();
        A a1 = xoManager.create(D.class);
        a1.setIndex("1");
        xoManager.currentTransaction().commit();
        xoManager.currentTransaction().begin();
        assertThat(xoManager.find(D.class, "1").iterator().next(), equalTo(a1));
        xoManager.currentTransaction().commit();
    }
}
