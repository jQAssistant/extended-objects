package com.buschmais.cdo.neo4j.test.mapping;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.api.bootstrap.CdoUnit;
import com.buschmais.cdo.neo4j.test.AbstractCdoManagerTest;
import com.buschmais.cdo.neo4j.test.mapping.composite.A;
import com.buschmais.cdo.neo4j.test.mapping.composite.B;
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

    public ReferencePropertyMappingTest(CdoUnit cdoUnit) {
        super(cdoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getCdoUnits() throws URISyntaxException {
        return cdoUnits(A.class, B.class);
    }

    @Test
    public void referenceProperty() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        A a = cdoManager.create(A.class);
        B b1 = cdoManager.create(B.class);
        B b2 = cdoManager.create(B.class);
        a.setB(b1);
        cdoManager.currentTransaction().commit();
        cdoManager.currentTransaction().begin();
        assertThat(a.getB(), equalTo(b1));
        a.setB(b2);
        cdoManager.currentTransaction().commit();
        cdoManager.currentTransaction().begin();
        assertThat(a.getB(), equalTo(b2));
        a.setB(null);
        cdoManager.currentTransaction().commit();
        cdoManager.currentTransaction().begin();
        assertThat(a.getB(), equalTo(null));
        cdoManager.currentTransaction().commit();
    }

    @Test
    public void mappedReferenceProperty() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        A a = cdoManager.create(A.class);
        B b = cdoManager.create(B.class);
        a.setMappedB(b);
        cdoManager.currentTransaction().commit();
        cdoManager.currentTransaction().begin();
        TestResult result = executeQuery("match (a:A)-[:MAPPED_B]->(b) return b");
        assertThat(result.getColumn("b"), hasItem(b));
        cdoManager.currentTransaction().commit();
    }

}
