package com.buschmais.cdo.neo4j.test.embedded.query;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.neo4j.test.embedded.AbstractEmbeddedCdoManagerTest;
import com.buschmais.cdo.neo4j.test.embedded.query.composite.A;
import com.buschmais.cdo.neo4j.test.embedded.query.composite.InstanceByValue;
import org.junit.Before;
import org.junit.Test;

import static com.buschmais.cdo.api.Query.Result;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class QueryReturnTypesTest extends AbstractEmbeddedCdoManagerTest {

    private A a;

    @Override
    protected Class<?>[] getTypes() {
        return new Class<?>[]{A.class};
    }

    @Before
    public void createData() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        a = cdoManager.create(A.class);
        a.setValue("A");
        cdoManager.currentTransaction().commit();
    }

    @Test
    public void cypherWithPrimitiveReturnType() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        Result<String> result = cdoManager.createQuery("match (a:A) return a.value", String.class).execute();
        assertThat(result.getSingleResult(), equalTo("A"));
        cdoManager.currentTransaction().commit();
    }

    @Test
    public void cypherWithEntityReturnType() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        Result<A> result = cdoManager.createQuery("match (a:A) return a", A.class).execute();
        assertThat(result.getSingleResult(), equalTo(a));
        cdoManager.currentTransaction().commit();
    }

    @Test
    public void typedQuery() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        Result<InstanceByValue> result = cdoManager.createQuery(InstanceByValue.class).withParameter("value", "A").execute();
        assertThat(result.getSingleResult().getA(), equalTo(a));
        cdoManager.currentTransaction().commit();
    }
}
