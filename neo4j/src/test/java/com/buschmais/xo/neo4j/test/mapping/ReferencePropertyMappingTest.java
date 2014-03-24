package com.buschmais.xo.neo4j.test.mapping;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractCdoManagerTest;
import com.buschmais.xo.neo4j.test.mapping.composite.A;
import com.buschmais.xo.neo4j.test.mapping.composite.B;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.net.URISyntaxException;
import java.util.Collection;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class ReferencePropertyMappingTest extends AbstractCdoManagerTest {

    public ReferencePropertyMappingTest(XOUnit XOUnit) {
        super(XOUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getCdoUnits() throws URISyntaxException {
        return cdoUnits(A.class, B.class);
    }

    @Test
    public void referenceProperty() {
        XOManager XOManager = getXOManager();
        XOManager.currentTransaction().begin();
        A a = XOManager.create(A.class);
        B b1 = XOManager.create(B.class);
        B b2 = XOManager.create(B.class);
        a.setB(b1);
        XOManager.currentTransaction().commit();
        XOManager.currentTransaction().begin();
        assertThat(a.getB(), equalTo(b1));
        a.setB(b2);
        XOManager.currentTransaction().commit();
        XOManager.currentTransaction().begin();
        assertThat(a.getB(), equalTo(b2));
        a.setB(null);
        XOManager.currentTransaction().commit();
        XOManager.currentTransaction().begin();
        assertThat(a.getB(), equalTo(null));
        XOManager.currentTransaction().commit();
    }

    @Test
    public void mappedReferenceProperty() {
        XOManager XOManager = getXOManager();
        XOManager.currentTransaction().begin();
        A a = XOManager.create(A.class);
        B b = XOManager.create(B.class);
        a.setMappedB(b);
        XOManager.currentTransaction().commit();
        XOManager.currentTransaction().begin();
        TestResult result = executeQuery("match (a:A)-[:MAPPED_B]->(b) return b");
        assertThat(result.getColumn("b"), hasItem(b));
        XOManager.currentTransaction().commit();
    }

}
