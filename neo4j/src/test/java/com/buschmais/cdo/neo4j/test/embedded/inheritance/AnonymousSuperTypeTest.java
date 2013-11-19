package com.buschmais.cdo.neo4j.test.embedded.inheritance;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.neo4j.test.embedded.AbstractEmbeddedCdoManagerTest;
import com.buschmais.cdo.neo4j.test.embedded.inheritance.composite.A;
import com.buschmais.cdo.neo4j.test.embedded.inheritance.composite.B;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class AnonymousSuperTypeTest extends AbstractEmbeddedCdoManagerTest {

    @Override
    protected Class<?>[] getTypes() {
        return new Class<?>[]{A.class, B.class};
    }

    @Test
    public void anonymousSuperType() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.begin();
        B b = cdoManager.create(B.class);
        b.setIndex("1");
        b.setVersion(1);
        cdoManager.commit();
        cdoManager.begin();
        A a = cdoManager.find(A.class, "1").iterator().next();
        assertThat(b, equalTo(a));
        assertThat(a.getVersion(), equalTo(1L));
        cdoManager.commit();
    }
}
