package com.buschmais.cdo.neo4j.test.embedded.relation.qualified;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.neo4j.test.embedded.AbstractEmbeddedCdoManagerTest;
import com.buschmais.cdo.neo4j.test.embedded.relation.qualified.composite.A;
import com.buschmais.cdo.neo4j.test.embedded.relation.qualified.composite.B;
import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertThat;

public class QualifiedRelationTest extends AbstractEmbeddedCdoManagerTest {

    @Override
    protected Class<?>[] getTypes() {
        return new Class<?>[]{A.class, B.class};
    }

    @Test
    @Ignore
    public void oneToOne() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        A a = cdoManager.create(A.class);
        B b1 = cdoManager.create(B.class);
        B b2 = cdoManager.create(B.class);
        a.setOneToOne(b1);
        cdoManager.currentTransaction().commit();
        cdoManager.currentTransaction().begin();
        assertThat(a.getOneToOne(), equalTo(b1));
        assertThat(executeQuery("MATCH (a:A)-[:QualifiedOneToOne]->(b:B) RETURN b").getColumn("b"), hasItem(b1));
        a.setOneToOne(b2);
        cdoManager.currentTransaction().commit();
        cdoManager.currentTransaction().begin();
        assertThat(a.getOneToOne(), equalTo(b2));
        assertThat(executeQuery("MATCH (a:A)-[:QualifiedOneToOne]->(b:B) RETURN b").getColumn("b"), hasItem(b2));
        a.setOneToOne(null);
        cdoManager.currentTransaction().commit();
        cdoManager.currentTransaction().begin();
        assertThat(a.getOneToOne(), equalTo(null));
        cdoManager.currentTransaction().commit();
    }
}
