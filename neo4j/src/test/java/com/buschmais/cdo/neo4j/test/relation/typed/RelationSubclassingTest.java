package com.buschmais.cdo.neo4j.test.relation.typed;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.api.bootstrap.CdoUnit;
import com.buschmais.cdo.neo4j.test.AbstractCdoManagerTest;
import com.buschmais.cdo.neo4j.test.relation.typed.composite.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.net.URISyntaxException;
import java.util.Collection;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class RelationSubclassingTest extends AbstractCdoManagerTest {

    public RelationSubclassingTest(CdoUnit cdoUnit) {
        super(cdoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getCdoUnits() throws URISyntaxException {
        return cdoUnits(C.class, D.class, TypeA.class, TypeB.class);
    }

    @Test
    public void testRelationSubclassing() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        C c = cdoManager.create(C.class);
        D d1 = cdoManager.create(D.class);
        D d2 = cdoManager.create(D.class);
        BaseType relation1 = cdoManager.create(c, TypeA.class, d1);
        relation1.setVersion(1);
        BaseType relation2 = cdoManager.create(c, TypeB.class, d2);
        relation2.setVersion(2);
        cdoManager.currentTransaction().commit();

        cdoManager.currentTransaction().begin();
        assertThat(c.getTypeA().getVersion(), equalTo(relation1.getVersion()));
        assertThat(c.getTypeB().getVersion(), equalTo(relation2.getVersion()));
        assertThat(relation1.getC(), equalTo(c));
        assertThat(relation1.getD(), equalTo(d1));
        assertThat(relation2.getC(), equalTo(c));
        assertThat(relation2.getD(), equalTo(d2));
        cdoManager.currentTransaction().commit();
    }

}
