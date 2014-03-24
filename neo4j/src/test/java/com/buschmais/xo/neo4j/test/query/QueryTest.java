package com.buschmais.xo.neo4j.test.query;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractXOManagerTest;
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
public class QueryTest extends AbstractXOManagerTest {

    private A a1;
    private A a2_1;
    private A a2_2;

    public QueryTest(XOUnit xoUnit) {
        super(xoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getXOUnits() throws URISyntaxException {
        return xoUnits(asList(Database.MEMORY), asList(A.class));
    }

    @Before
    public void createData() {
        XOManager xoManager = getXoManager();
        xoManager.currentTransaction().begin();
        a1 = xoManager.create(A.class);
        a1.setValue("A1");
        a2_1 = xoManager.create(A.class);
        a2_1.setValue("A2");
        a2_2 = xoManager.create(A.class);
        a2_2.setValue("A2");
        xoManager.currentTransaction().commit();
    }

    @Test
    public void cypherStringQuery() {
        XOManager xoManager = getXoManager();
        xoManager.currentTransaction().begin();
        Result<CompositeRowObject> result = xoManager.createQuery("match (a:A) where a.value={value} return a").withParameter("value", "A1").execute();
        A a = result.getSingleResult().get("a", A.class);
        assertThat(a.getValue(), equalTo("A1"));
        result = xoManager.createQuery("match (a:A) where a.Value={value} return a").withParameter("value", "A2").execute();
        try {
            result.getSingleResult().get("a", A.class);
            fail("Expecting a " + XOException.class.getName());
        } catch (XOException e) {
        }
        xoManager.currentTransaction().commit();
    }

    @Test
    public void cypherStringQuerySimple() {
        XOManager xoManager = getXoManager();

        xoManager.currentTransaction().begin();
        Result<CompositeRowObject> result = xoManager.createQuery("MATCH (a:A) RETURN a.value LIMIT 1").execute();
        assertEquals("A1", result.getSingleResult().as(String.class));

        Result<CompositeRowObject> longResult = xoManager.createQuery("MATCH (a:A) RETURN 10 LIMIT 1").execute();
        assertEquals(10L, (long) longResult.getSingleResult().as(Long.class));

        xoManager.currentTransaction().commit();
    }

    @Test
    public void compositeRowTypedQuery() {
        XOManager xoManager = getXoManager();
        xoManager.currentTransaction().begin();
        Result<InstanceByValue> result = xoManager.createQuery(InstanceByValue.class).withParameter("value", "A1").execute();
        A a = result.getSingleResult().getA();
        assertThat(a.getValue(), equalTo("A1"));
        result = xoManager.createQuery(InstanceByValue.class).withParameter("value", "A2").execute();
        try {
            result.getSingleResult().getA();
            fail("Expecting a " + XOException.class.getName());
        } catch (XOException e) {
        }
        xoManager.currentTransaction().commit();
    }

    @Test
    public void typedQuery() {
        XOManager xoManager = getXoManager();
        xoManager.currentTransaction().begin();
        Result<InstanceByValue> result = xoManager.createQuery(InstanceByValue.class).withParameter("value", "A1").execute();
        A a = result.getSingleResult().getA();
        assertThat(a.getValue(), equalTo("A1"));
        xoManager.currentTransaction().commit();
    }

    @Test
    public void instanceParameter() {
        XOManager xoManager = getXoManager();
        xoManager.currentTransaction().begin();
        Result<CompositeRowObject> row = xoManager.createQuery("match (a:A) where a={instance} return a").withParameter("instance", a1).execute();
        A a = row.getSingleResult().get("a", A.class);
        assertThat(a, equalTo(a1));
        xoManager.currentTransaction().commit();
    }

    @Test
    public void optionalMatch() {
        XOManager xoManager = getXoManager();
        xoManager.currentTransaction().begin();
        Result<CompositeRowObject> row = getXoManager().createQuery("OPTIONAL MATCH (a:A) WHERE a.name = 'X' return a").execute();
        A a = row.getSingleResult().get("a", A.class);
        assertThat(a, equalTo(null));
        xoManager.currentTransaction().commit();
    }

}
