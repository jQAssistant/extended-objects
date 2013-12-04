package com.buschmais.cdo.neo4j.test.embedded.mapping;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.neo4j.test.embedded.AbstractEmbeddedCdoManagerTest;
import com.buschmais.cdo.neo4j.test.embedded.mapping.composite.A;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertThat;

public class PrimitivePropertyMappingTest extends AbstractEmbeddedCdoManagerTest {

    @Override
    protected Class<?>[] getTypes() {
        return new Class<?>[]{A.class};
    }

    @Test
    public void primitiveProperty() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        A a = cdoManager.create(A.class);
        a.setString("value");
        cdoManager.currentTransaction().commit();
        cdoManager.currentTransaction().begin();
        assertThat(a.getString(), equalTo("value"));
        a.setString("updatedValue");
        cdoManager.currentTransaction().commit();
        cdoManager.currentTransaction().begin();
        assertThat(a.getString(), equalTo("updatedValue"));
        a.setString(null);
        cdoManager.currentTransaction().commit();
        cdoManager.currentTransaction().begin();
        assertThat(a.getString(), equalTo(null));
        cdoManager.currentTransaction().commit();
    }

    @Test
    public void mappedPrimitiveProperty() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        A a = cdoManager.create(A.class);
        a.setMappedString("mappedValue");
        cdoManager.currentTransaction().commit();
        cdoManager.currentTransaction().begin();
        TestResult result = executeQuery("match (a:A) return a.MAPPED_STRING as v");
        assertThat(result.getColumn("v"), hasItem("mappedValue"));
        cdoManager.currentTransaction().commit();
    }
}
