package com.buschmais.xo.neo4j.test.query;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractCdoManagerTest;
import com.buschmais.xo.neo4j.test.query.composite.A;
import com.buschmais.xo.neo4j.test.query.composite.InstanceByValue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.net.URISyntaxException;
import java.util.Collection;

import static com.buschmais.xo.api.Query.Result;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class QueryReturnTypesTest extends AbstractCdoManagerTest {

    private A a;

    public QueryReturnTypesTest(XOUnit XOUnit) {
        super(XOUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getCdoUnits() throws URISyntaxException {
        return cdoUnits(A.class);
    }

    @Before
    public void createData() {
        XOManager XOManager = getXOManager();
        XOManager.currentTransaction().begin();
        a = XOManager.create(A.class);
        a.setValue("A");
        XOManager.currentTransaction().commit();
    }

    @Test
    public void cypherWithPrimitiveReturnType() {
        XOManager XOManager = getXOManager();
        XOManager.currentTransaction().begin();
        Result<String> result = XOManager.createQuery("match (a:A) return a.value", String.class).execute();
        assertThat(result.getSingleResult(), equalTo("A"));
        XOManager.currentTransaction().commit();
    }

    @Test
    public void cypherWithEntityReturnType() {
        XOManager XOManager = getXOManager();
        XOManager.currentTransaction().begin();
        Result<A> result = XOManager.createQuery("match (a:A) return a", A.class).execute();
        assertThat(result.getSingleResult(), equalTo(a));
        XOManager.currentTransaction().commit();
    }

    @Test
    public void typedQuery() {
        XOManager XOManager = getXOManager();
        XOManager.currentTransaction().begin();
        Result<InstanceByValue> result = XOManager.createQuery(InstanceByValue.class).withParameter("value", "A").execute();
        assertThat(result.getSingleResult().getA(), equalTo(a));
        XOManager.currentTransaction().commit();
    }
}
