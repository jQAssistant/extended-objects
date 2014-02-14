package com.buschmais.cdo.neo4j.test.inheritance;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.api.bootstrap.CdoUnit;
import com.buschmais.cdo.neo4j.test.AbstractCdoManagerTest;
import com.buschmais.cdo.neo4j.test.inheritance.composite.A;
import com.buschmais.cdo.neo4j.test.inheritance.composite.D;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.net.URISyntaxException;
import java.util.Collection;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class AnonymousSubTypeTest extends AbstractCdoManagerTest {

    public AnonymousSubTypeTest(CdoUnit cdoUnit) {
        super(cdoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getCdoUnits() throws URISyntaxException {
        return cdoUnits(D.class);
    }

    @Test
    public void anonymousSubType() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        D b = cdoManager.create(D.class);
        b.setIndex("1");
        cdoManager.currentTransaction().commit();
        closeCdoManager();
        cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        A a = cdoManager.find(A.class, "1").iterator().next();
        assertThat(a.getIndex(), equalTo("1"));
        cdoManager.currentTransaction().commit();
    }

}
