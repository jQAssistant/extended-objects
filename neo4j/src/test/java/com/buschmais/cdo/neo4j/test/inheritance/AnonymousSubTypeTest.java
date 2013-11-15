package com.buschmais.cdo.neo4j.test.inheritance;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.neo4j.test.AbstractCdoManagerTest;
import com.buschmais.cdo.neo4j.test.inheritance.composite.A;
import com.buschmais.cdo.neo4j.test.inheritance.composite.B;
import com.buschmais.cdo.neo4j.test.inheritance.composite.D;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class AnonymousSubTypeTest extends AbstractCdoManagerTest {

    @Override
    protected Class<?>[] getTypes() {
        return new Class<?>[]{D.class};
    }

    @Test
    public void anonymousSuperType() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.begin();
        D b = cdoManager.create(D.class);
        b.setIndex("1");
        cdoManager.commit();
        closeCdoManager();
        cdoManager = getCdoManager();
        cdoManager.begin();
        A a = cdoManager.find(A.class, "1").iterator().next();
        assertThat(a.getIndex(), equalTo("1"));
        cdoManager.commit();
    }

}
