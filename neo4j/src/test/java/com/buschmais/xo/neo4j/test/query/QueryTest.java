package com.buschmais.xo.neo4j.test.query;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.net.URISyntaxException;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.buschmais.xo.api.Query.Result;
import com.buschmais.xo.api.Query.Result.CompositeRowObject;
import com.buschmais.xo.api.XOException;
import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.api.annotation.Cypher;
import com.buschmais.xo.neo4j.api.query.CypherQuery;
import com.buschmais.xo.neo4j.test.AbstractNeo4jXOManagerTest;
import com.buschmais.xo.neo4j.test.query.composite.A;
import com.buschmais.xo.neo4j.test.query.composite.InstanceByValue;

@RunWith(Parameterized.class)
public class QueryTest extends AbstractNeo4jXOManagerTest {

    private A a1;
    private A a2_1;
    private A a2_2;

    public QueryTest(final XOUnit xoUnit) {
        super(xoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getXOUnits() throws URISyntaxException {
        return xoUnits(A.class);
    }

    @Before
    public void createData() {
        final XOManager xoManager = getXoManager();
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
    public void typedQuery() {
        final XOManager xoManager = getXoManager();
        xoManager.currentTransaction().begin();
        final Result<InstanceByValue> result = xoManager.createQuery(InstanceByValue.class).withParameter("value", "A1").execute();
        final A a = result.getSingleResult().getA();
        assertThat(a.getValue(), equalTo("A1"));
        xoManager.currentTransaction().commit();
    }

    @Test
    public void compositeRow() {
        final XOManager xoManager = getXoManager();
        xoManager.currentTransaction().begin();
        final CompositeRowObject result = xoManager.createQuery("match (a:A) where a.value={value} return a, count(a) as count", ResultPart1.class, ResultPart2.class).withParameter("value", "A1").execute().getSingleResult();
        final A a = result.as(ResultPart1.class).getA();
        assertThat(a.getValue(), equalTo("A1"));
        final Number count = result.as(ResultPart2.class).getCount();
        assertThat(count.intValue(), equalTo(1));
        try {
            xoManager.createQuery(ResultPart1.class, ResultPart2.class).withParameter("value", "A2").execute().getSingleResult();
            fail("Expecting a " + XOException.class.getName());
        } catch (final XOException e) {
        }
        xoManager.currentTransaction().commit();
    }

    @Test
    public void compositeRowAsMap() {
        final XOManager xoManager = getXoManager();
        xoManager.currentTransaction().begin();
        Result<CompositeRowObject> result = xoManager.createQuery("match (a:A) where a.value={value} return a").withParameter("value", "A1").execute();
        final A a = result.getSingleResult().get("a", A.class);
        assertThat(a.getValue(), equalTo("A1"));
        result = xoManager.createQuery("match (a:A) where a.Value={value} return a").withParameter("value", "A2").execute();
        try {
            result.getSingleResult().get("a", A.class);
            fail("Expecting a " + XOException.class.getName());
        } catch (final XOException e) {
        }
        xoManager.currentTransaction().commit();
    }

    @Test
    public void instanceParameter() {
        final XOManager xoManager = getXoManager();
        xoManager.currentTransaction().begin();
        final Result<CompositeRowObject> row = xoManager.createQuery("match (a:A) where id(a)={instance} return a").withParameter("instance", a1).execute();
        final A a = row.getSingleResult().get("a", A.class);
        assertThat(a, equalTo(a1));
        xoManager.currentTransaction().commit();
    }


    @Test
    public void optionalMatch() {
        final XOManager xoManager = getXoManager();
        xoManager.currentTransaction().begin();
        final Result<CompositeRowObject> row = getXoManager().createQuery("OPTIONAL MATCH (a:A) WHERE a.name = 'X' return a").execute();
        final A a = row.getSingleResult().get("a", A.class);
        assertThat(a, equalTo(null));
        xoManager.currentTransaction().commit();
    }

    public void testTypedCypherQueryLanguage() {
        final XOManager xoManager = getXoManager();
        xoManager.currentTransaction().begin();
        final Result<CompositeRowObject> result = xoManager.createQuery(new CypherQuery("MATCH (a:A) return a")).execute();
        assertThat(result.hasResult(), is(true));
        xoManager.currentTransaction().commit();
    }

    public void testTypedCypherQueryLanguageWithReturnType() {
        final XOManager xoManager = getXoManager();
        xoManager.currentTransaction().begin();
        final Result<A> result = xoManager.createQuery(new CypherQuery("MATCH (a:A) return a"), A.class).execute();
        assertThat(result.hasResult(), is(true));
        xoManager.currentTransaction().commit();
    }

    public void testUsingCypherQueryLanguage() {
        final XOManager xoManager = getXoManager();
        xoManager.currentTransaction().begin();
        final Result<CompositeRowObject> result = xoManager.createQuery("MATCH (a:A) return a").using(Cypher.class).execute();
        assertThat(result.hasResult(), is(true));
        xoManager.currentTransaction().commit();
    }

    public interface ResultPart1 {
        A getA();
    }

    public interface ResultPart2 {
        Number getCount();
    }
}
