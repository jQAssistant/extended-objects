package com.buschmais.cdo.neo4j.test.query;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.api.bootstrap.CdoUnit;
import com.buschmais.cdo.neo4j.test.AbstractCdoManagerTest;
import com.buschmais.cdo.neo4j.test.query.composite.A;
import com.buschmais.cdo.neo4j.test.query.composite.InstanceByValue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.net.URISyntaxException;
import java.util.Collection;

import static com.buschmais.cdo.api.Query.Result;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class QueryReturnTypesTest extends AbstractCdoManagerTest {

    private A a;

    public QueryReturnTypesTest(CdoUnit cdoUnit) {
        super(cdoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getCdoUnits() throws URISyntaxException {
        return cdoUnits(A.class);
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
