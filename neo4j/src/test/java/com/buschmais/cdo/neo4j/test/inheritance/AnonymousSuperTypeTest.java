package com.buschmais.cdo.neo4j.test.inheritance;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.api.bootstrap.CdoUnit;
import com.buschmais.cdo.neo4j.test.AbstractCdoManagerTest;
import com.buschmais.cdo.neo4j.test.inheritance.composite.A;
import com.buschmais.cdo.neo4j.test.inheritance.composite.B;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.net.URISyntaxException;
import java.util.Collection;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class AnonymousSuperTypeTest extends AbstractCdoManagerTest {

    public AnonymousSuperTypeTest(CdoUnit cdoUnit) {
        super(cdoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getCdoUnits() throws URISyntaxException {
        return cdoUnits(A.class, B.class);
    }

    @Test
    public void anonymousSuperType() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        B b = cdoManager.create(B.class);
        b.setIndex("1");
        b.setVersion(1);
        cdoManager.currentTransaction().commit();
        cdoManager.currentTransaction().begin();
        A a = cdoManager.find(A.class, "1").iterator().next();
        assertThat(b, equalTo(a));
        assertThat(a.getVersion().longValue(), equalTo(1L));
        cdoManager.currentTransaction().commit();
    }
}
