package com.buschmais.cdo.neo4j.test.embedded.relation.typed;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.neo4j.test.embedded.AbstractEmbeddedCdoManagerTest;
import com.buschmais.cdo.neo4j.test.embedded.relation.typed.composite.A;
import com.buschmais.cdo.neo4j.test.embedded.relation.typed.composite.B;
import com.buschmais.cdo.neo4j.test.embedded.relation.typed.composite.TypedOneToOneRelation;
import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class TypedReferencePropertyTest extends AbstractEmbeddedCdoManagerTest {

    @Override
    protected Class<?>[] getTypes() {
        return new Class<?>[]{A.class, B.class, TypedOneToOneRelation.class};
    }

    @Test
    @Ignore
    public void oneToOne() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        A a = cdoManager.create(A.class);
        B b1 = cdoManager.create(B.class);
        B b2 = cdoManager.create(B.class);
        TypedOneToOneRelation relation1 = cdoManager.create(a, TypedOneToOneRelation.class, b1);
        cdoManager.currentTransaction().commit();
        cdoManager.currentTransaction().begin();
        assertThat(a.getOneToOne(), equalTo(relation1));
        TypedOneToOneRelation relation2 = cdoManager.create(a, TypedOneToOneRelation.class, b2);
        cdoManager.currentTransaction().commit();
        cdoManager.currentTransaction().begin();
        assertThat(a.getOneToOne(), equalTo(relation2));
        cdoManager.delete(relation2);
        cdoManager.currentTransaction().commit();
        cdoManager.currentTransaction().begin();
        assertThat(a.getOneToOne(), equalTo(null));
        cdoManager.currentTransaction().commit();
    }
}
