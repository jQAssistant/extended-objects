package com.buschmais.xo.neo4j.test.query;

import static com.buschmais.xo.api.Query.Result;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Collection;
import java.util.Map;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractNeo4JXOManagerIT;
import com.buschmais.xo.neo4j.test.query.composite.A;
import com.buschmais.xo.neo4j.test.query.composite.InstanceByValue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class QueryReturnTypesIT extends AbstractNeo4JXOManagerIT {

    private A a;

    public QueryReturnTypesIT(XOUnit xoUnit) {
        super(xoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getXOUnits() {
        return xoUnits(A.class);
    }

    @Before
    public void createData() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction().begin();
        a = xoManager.create(A.class);
        a.setValue("A");
        xoManager.currentTransaction().commit();
    }

    @Test
    public void cypherWithPrimitiveReturnType() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction().begin();
        Result<String> result = xoManager.createQuery("match (a:A) return a.value", String.class).execute();
        assertThat(result.getSingleResult(), equalTo("A"));
        xoManager.currentTransaction().commit();
    }

    @Test
    public void cypherWithEntityReturnType() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction().begin();
        Result<A> result = xoManager.createQuery("match (a:A) return a", A.class).execute();
        assertThat(result.getSingleResult(), equalTo(a));
        xoManager.currentTransaction().commit();
    }

    @Test
    public void cypherWithJsonReturnType() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction().begin();
        Result<Map> result = xoManager.createQuery("match (a:A) return {node: a}", Map.class).execute();
        assertThat(result.getSingleResult().get("node"), equalTo(a));
        xoManager.currentTransaction().commit();
    }

    @Test
    public void typedQuery() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction().begin();
        Result<InstanceByValue> result = xoManager.createQuery(InstanceByValue.class).withParameter("value", "A").execute();
        assertThat(result.getSingleResult().getA(), equalTo(a));
        xoManager.currentTransaction().commit();
    }
}
