package com.buschmais.xo.neo4j.test.batchable;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeThat;

import java.util.Collection;
import java.util.concurrent.ExecutionException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractNeo4jXOManagerTest;
import com.buschmais.xo.neo4j.test.Neo4jDatabase;
import com.buschmais.xo.neo4j.test.batchable.composite.A;
import com.buschmais.xo.neo4j.test.batchable.composite.A2B;
import com.buschmais.xo.neo4j.test.batchable.composite.B;

@RunWith(Parameterized.class)
public class BatchableTest extends AbstractNeo4jXOManagerTest {

    public BatchableTest(XOUnit xoUnit) {
        super(xoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getXOUnits() {
        return xoUnits(A.class, A2B.class, B.class);
    }

    @Test
    public void batchable() throws ExecutionException, InterruptedException {
        assumeThat(getXoManagerFactory().getXOUnit().getProvider(), equalTo(Neo4jDatabase.BOLT.getProvider()));
        XOManager xoManager = getXoManager();
        xoManager.currentTransaction().begin();
        A a = xoManager.create(A.class);
        a.setName("A");
        assertThat(xoManager.getId(a), lessThan(0l));
        B b = xoManager.create(B.class);
        b.setName("B");
        assertThat(xoManager.getId(b), lessThan(0l));
        A2B a2b = xoManager.create(a, A2B.class, b);
        assertThat(xoManager.getId(a2b), lessThan(0l));
        xoManager.currentTransaction().commit();
        xoManager.currentTransaction().begin();
        a = xoManager.find(A.class, "A").getSingleResult();
        assertThat(xoManager.getId(a), greaterThanOrEqualTo(0l));

        a2b = a.getA2B();
        assertThat(xoManager.getId(a2b), greaterThanOrEqualTo(0l));

        b = xoManager.find(B.class, "B").getSingleResult();
        assertThat(xoManager.getId(b), greaterThanOrEqualTo(0l));

        assertThat(b.getA2B(), is(a2b));

        xoManager.currentTransaction().commit();
    }

}
