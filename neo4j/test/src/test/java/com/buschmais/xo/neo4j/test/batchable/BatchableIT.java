package com.buschmais.xo.neo4j.test.batchable;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assume.assumeThat;

import java.util.Collection;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractNeo4JXOManagerIT;
import com.buschmais.xo.neo4j.test.Neo4jDatabase;
import com.buschmais.xo.neo4j.test.batchable.composite.A;
import com.buschmais.xo.neo4j.test.batchable.composite.A2B;
import com.buschmais.xo.neo4j.test.batchable.composite.B;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class BatchableIT extends AbstractNeo4JXOManagerIT {

    public BatchableIT(XOUnit xoUnit) {
        super(xoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getXOUnits() {
        return xoUnits(A.class, A2B.class, B.class);
    }

    @Test
    public void batchable() {
        assumeThat(getXOManagerFactory().getXOUnit().getProvider(), equalTo(Neo4jDatabase.BOLT.getProvider()));
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction().begin();
        A a = xoManager.create(A.class);
        a.setName("A1");
        assertThat(xoManager.getId(a), lessThan(0l));
        B b = xoManager.create(B.class);
        b.setName("B");
        assertThat(xoManager.getId(b), lessThan(0l));
        A2B a2b = xoManager.create(a, A2B.class, b);
        a2b.setValue(1);
        assertThat(xoManager.getId(a2b), lessThan(0l));
        xoManager.currentTransaction().commit();
        xoManager.currentTransaction().begin();
        assertThat(xoManager.getId(a), greaterThanOrEqualTo(0l));
        assertThat(xoManager.getId(a2b), greaterThanOrEqualTo(0l));
        assertThat(xoManager.getId(b), greaterThanOrEqualTo(0l));
        assertThat(a.getName(), equalTo("A1"));
        assertThat(b.getName(), equalTo("B"));
        assertThat(a2b.getValue(), equalTo(1));
        a.setName("A2");
        a2b.setValue(2);
        xoManager.currentTransaction().commit();
        xoManager.currentTransaction().begin();
        a = xoManager.find(A.class, "A2").getSingleResult();
        a2b = a.getA2B();
        assertThat(a2b.getValue(), equalTo(2));
        b = xoManager.find(B.class, "B").getSingleResult();
        assertThat(b.getA2B(), is(a2b));
        xoManager.currentTransaction().commit();
    }

}
