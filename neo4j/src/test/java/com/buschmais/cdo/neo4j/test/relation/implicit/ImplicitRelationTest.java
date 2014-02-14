package com.buschmais.cdo.neo4j.test.relation.implicit;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.api.bootstrap.CdoUnit;
import com.buschmais.cdo.neo4j.test.AbstractCdoManagerTest;
import com.buschmais.cdo.neo4j.test.relation.implicit.composite.A;
import com.buschmais.cdo.neo4j.test.relation.implicit.composite.B;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.net.URISyntaxException;
import java.util.Collection;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class ImplicitRelationTest extends AbstractCdoManagerTest {

    public ImplicitRelationTest(CdoUnit cdoUnit) {
        super(cdoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getCdoUnits() throws URISyntaxException {
        return cdoUnits(A.class, B.class);
    }

    @Test
    public void oneToOne() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        A a = cdoManager.create(A.class);
        B b1 = cdoManager.create(B.class);
        a.setOneToOne(b1);
        cdoManager.currentTransaction().commit();
        cdoManager.currentTransaction().begin();
        assertThat(a.getOneToOne(), equalTo(b1));
        assertThat(b1.getOneToOne(), equalTo(a));
        assertThat(executeQuery("MATCH (a:A)-[:ImplicitOneToOne]->(b:B) RETURN b").getColumn("b"), hasItem(b1));
        B b2 = cdoManager.create(B.class);
        a.setOneToOne(b2);
        cdoManager.currentTransaction().commit();
        cdoManager.currentTransaction().begin();
        assertThat(a.getOneToOne(), equalTo(b2));
        assertThat(b2.getOneToOne(), equalTo(a));
        assertThat(b1.getOneToOne(), equalTo(null));
        assertThat(executeQuery("MATCH (a:A)-[:ImplicitOneToOne]->(b:B) RETURN b").getColumn("b"), hasItem(b2));
        a.setOneToOne(null);
        cdoManager.currentTransaction().commit();
        cdoManager.currentTransaction().begin();
        assertThat(a.getOneToOne(), equalTo(null));
        assertThat(b1.getOneToOne(), equalTo(null));
        assertThat(b2.getOneToOne(), equalTo(null));
        cdoManager.currentTransaction().commit();
    }
}
