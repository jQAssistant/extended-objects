package com.buschmais.xo.neo4j.test.query;

import com.buschmais.xo.api.XOException;
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
import static com.buschmais.xo.api.Query.Result.CompositeRowObject;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class QueryTest extends AbstractCdoManagerTest {

    private A a1;
    private A a2_1;
    private A a2_2;

    public QueryTest(XOUnit XOUnit) {
        super(XOUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getCdoUnits() throws URISyntaxException {
        return cdoUnits(asList(Database.MEMORY), asList(A.class));
    }

    @Before
    public void createData() {
        XOManager XOManager = getXOManager();
        XOManager.currentTransaction().begin();
        a1 = XOManager.create(A.class);
        a1.setValue("A1");
        a2_1 = XOManager.create(A.class);
        a2_1.setValue("A2");
        a2_2 = XOManager.create(A.class);
        a2_2.setValue("A2");
        XOManager.currentTransaction().commit();
    }

    @Test
    public void cypherStringQuery() {
        XOManager XOManager = getXOManager();
        XOManager.currentTransaction().begin();
        Result<CompositeRowObject> result = XOManager.createQuery("match (a:A) where a.value={value} return a").withParameter("value", "A1").execute();
        A a = result.getSingleResult().get("a", A.class);
        assertThat(a.getValue(), equalTo("A1"));
        result = XOManager.createQuery("match (a:A) where a.Value={value} return a").withParameter("value", "A2").execute();
        try {
            result.getSingleResult().get("a", A.class);
            fail("Expecting a " + XOException.class.getName());
        } catch (XOException e) {
        }
        XOManager.currentTransaction().commit();
    }

    @Test
    public void cypherStringQuerySimple() {
        XOManager XOManager = getXOManager();

        XOManager.currentTransaction().begin();
        Result<CompositeRowObject> result = XOManager.createQuery("MATCH (a:A) RETURN a.value LIMIT 1").execute();
        assertEquals("A1", result.getSingleResult().as(String.class));

        Result<CompositeRowObject> longResult = XOManager.createQuery("MATCH (a:A) RETURN 10 LIMIT 1").execute();
        assertEquals(10L, (long) longResult.getSingleResult().as(Long.class));

        XOManager.currentTransaction().commit();
    }

    @Test
    public void compositeRowTypedQuery() {
        XOManager XOManager = getXOManager();
        XOManager.currentTransaction().begin();
        Result<InstanceByValue> result = XOManager.createQuery(InstanceByValue.class).withParameter("value", "A1").execute();
        A a = result.getSingleResult().getA();
        assertThat(a.getValue(), equalTo("A1"));
        result = XOManager.createQuery(InstanceByValue.class).withParameter("value", "A2").execute();
        try {
            result.getSingleResult().getA();
            fail("Expecting a " + XOException.class.getName());
        } catch (XOException e) {
        }
        XOManager.currentTransaction().commit();
    }

    @Test
    public void typedQuery() {
        XOManager XOManager = getXOManager();
        XOManager.currentTransaction().begin();
        Result<InstanceByValue> result = XOManager.createQuery(InstanceByValue.class).withParameter("value", "A1").execute();
        A a = result.getSingleResult().getA();
        assertThat(a.getValue(), equalTo("A1"));
        XOManager.currentTransaction().commit();
    }

    @Test
    public void instanceParameter() {
        XOManager XOManager = getXOManager();
        XOManager.currentTransaction().begin();
        Result<CompositeRowObject> row = XOManager.createQuery("match (a:A) where a={instance} return a").withParameter("instance", a1).execute();
        A a = row.getSingleResult().get("a", A.class);
        assertThat(a, equalTo(a1));
        XOManager.currentTransaction().commit();
    }

    @Test
    public void optionalMatch() {
        XOManager XOManager = getXOManager();
        XOManager.currentTransaction().begin();
        Result<CompositeRowObject> row = getXOManager().createQuery("OPTIONAL MATCH (a:A) WHERE a.name = 'X' return a").execute();
        A a = row.getSingleResult().get("a", A.class);
        assertThat(a, equalTo(null));
        XOManager.currentTransaction().commit();
    }

}
