package com.buschmais.cdo.neo4j.test.embedded.mapping;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.neo4j.test.embedded.AbstractEmbeddedCdoManagerTest;
import com.buschmais.cdo.neo4j.test.embedded.mapping.composite.A;
import com.buschmais.cdo.neo4j.test.embedded.mapping.composite.Enumeration;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class EnumPropertyMappingTest extends AbstractEmbeddedCdoManagerTest {

    @Override
    protected Class<?>[] getTypes() {
        return new Class<?>[]{A.class};
    }

    @Test
    public void enumerationLabel() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.begin();
        A a = cdoManager.create(A.class);
        a.setEnumerationLabel(Enumeration.FIRST);
        cdoManager.commit();
        cdoManager.begin();
        assertThat(a.getEnumerationLabel(), equalTo(Enumeration.FIRST));
        a.setEnumerationLabel(Enumeration.SECOND);
        cdoManager.commit();
        cdoManager.begin();
        assertThat(a.getEnumerationLabel(), equalTo(Enumeration.SECOND));
        a.setEnumerationLabel(null);
        cdoManager.commit();
        cdoManager.begin();
        assertThat(a.getEnumerationLabel(), equalTo(null));
        cdoManager.commit();
    }

    @Test
    public void enumerationProperty() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.begin();
        A a = cdoManager.create(A.class);
        a.setEnumerationProperty(Enumeration.FIRST);
        cdoManager.commit();
        cdoManager.begin();
        assertThat(a.getEnumerationProperty(), equalTo(Enumeration.FIRST));
        a.setEnumerationProperty(Enumeration.SECOND);
        cdoManager.commit();
        cdoManager.begin();
        assertThat(a.getEnumerationProperty(), equalTo(Enumeration.SECOND));
        a.setEnumerationProperty(null);
        cdoManager.commit();
        cdoManager.begin();
        assertThat(a.getEnumerationProperty(), equalTo(null));
        cdoManager.commit();
    }
}
