package com.buschmais.cdo.neo4j.test.mapping;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.neo4j.test.AbstractCdoManagerTest;
import com.buschmais.cdo.neo4j.test.mapping.composite.A;
import com.buschmais.cdo.neo4j.test.mapping.composite.B;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class ReferencePropertyMappingTest extends AbstractCdoManagerTest {

    @Override
    protected Class<?>[] getTypes() {
        return new Class<?>[]{A.class, B.class};
    }

    @Test
    public void referenceProperty() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.begin();
        A a = cdoManager.create(A.class);
        B b1 = cdoManager.create(B.class);
        B b2 = cdoManager.create(B.class);
        a.setB(b1);
        cdoManager.commit();
        cdoManager.begin();
        assertThat(a.getB(), equalTo(b1));
        a.setB(b2);
        cdoManager.commit();
        cdoManager.begin();
        assertThat(a.getB(), equalTo(b2));
        a.setB(null);
        cdoManager.commit();
        cdoManager.begin();
        assertThat(a.getB(), equalTo(null));
        cdoManager.commit();
    }

}
