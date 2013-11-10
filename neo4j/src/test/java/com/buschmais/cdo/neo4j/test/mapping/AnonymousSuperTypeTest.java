package com.buschmais.cdo.neo4j.test.mapping;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.neo4j.test.AbstractCdoManagerTest;
import com.buschmais.cdo.neo4j.test.mapping.composite.A;
import com.buschmais.cdo.neo4j.test.mapping.composite.C;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class AnonymousSuperTypeTest extends AbstractCdoManagerTest {

    @Override
    protected Class<?>[] getTypes() {
        return new Class<?>[]{A.class, C.class};
    }

    @Test
    public void anonymousSuperType() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.begin();
        C c = cdoManager.create(C.class);
        c.setIndex("1");
        c.setVersion(1);
        cdoManager.commit();
        cdoManager.begin();
        A a = cdoManager.find(A.class, "1").iterator().next();
        assertThat(c, equalTo(a));
        assertThat(a.getVersion(), equalTo(1L));
        cdoManager.commit();
    }

}
