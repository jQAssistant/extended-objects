package com.buschmais.cdo.neo4j.test.mapping;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.neo4j.test.AbstractCdoManagerTest;
import com.buschmais.cdo.neo4j.test.mapping.composite.A;
import com.buschmais.cdo.neo4j.test.mapping.composite.Enumeration;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class EnumPropertyMappingTest extends AbstractCdoManagerTest {

    @Override
    protected Class<?>[] getTypes() {
        return new Class<?>[]{A.class};
    }

    @Test
    public void enumerationProperty() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.begin();
        A a = cdoManager.create(A.class);
        a.setEnumeratedValue(Enumeration.FIRST);
        cdoManager.commit();
        cdoManager.begin();
        assertThat(a.getEnumeratedValue(), equalTo(Enumeration.FIRST));
        a.setEnumeratedValue(Enumeration.SECOND);
        cdoManager.commit();
        cdoManager.begin();
        assertThat(a.getEnumeratedValue(), equalTo(Enumeration.SECOND));
        a.setEnumeratedValue(null);
        cdoManager.commit();
        cdoManager.begin();
        assertThat(a.getEnumeratedValue(), equalTo(null));
        cdoManager.commit();
    }
}
