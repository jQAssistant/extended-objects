package com.buschmais.xo.neo4j.test.mapping;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractCdoManagerTest;
import com.buschmais.xo.neo4j.test.mapping.composite.A;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.net.URISyntaxException;
import java.util.Collection;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class PrimitivePropertyMappingTest extends AbstractCdoManagerTest {

    public PrimitivePropertyMappingTest(XOUnit XOUnit) {
        super(XOUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getCdoUnits() throws URISyntaxException {
        return cdoUnits(A.class);
    }

    @Test
    public void primitiveProperty() {
        XOManager XOManager = getXOManager();
        XOManager.currentTransaction().begin();
        A a = XOManager.create(A.class);
        a.setString("value");
        XOManager.currentTransaction().commit();
        XOManager.currentTransaction().begin();
        assertThat(a.getString(), equalTo("value"));
        a.setString("updatedValue");
        XOManager.currentTransaction().commit();
        XOManager.currentTransaction().begin();
        assertThat(a.getString(), equalTo("updatedValue"));
        a.setString(null);
        XOManager.currentTransaction().commit();
        XOManager.currentTransaction().begin();
        assertThat(a.getString(), equalTo(null));
        XOManager.currentTransaction().commit();
    }

    @Test
    public void mappedPrimitiveProperty() {
        XOManager XOManager = getXOManager();
        XOManager.currentTransaction().begin();
        A a = XOManager.create(A.class);
        a.setMappedString("mappedValue");
        XOManager.currentTransaction().commit();
        XOManager.currentTransaction().begin();
        TestResult result = executeQuery("match (a:A) return a.MAPPED_STRING as v");
        assertThat(result.getColumn("v"), hasItem("mappedValue"));
        XOManager.currentTransaction().commit();
    }
}
