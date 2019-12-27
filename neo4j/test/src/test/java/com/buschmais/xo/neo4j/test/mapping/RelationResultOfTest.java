package com.buschmais.xo.neo4j.test.mapping;

import static com.buschmais.xo.api.Query.Result;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertThat;

import java.util.Collection;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractNeo4jXOManagerTest;
import com.buschmais.xo.neo4j.test.mapping.composite.E;
import com.buschmais.xo.neo4j.test.mapping.composite.E2F;
import com.buschmais.xo.neo4j.test.mapping.composite.F;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class RelationResultOfTest extends AbstractNeo4jXOManagerTest {

    private E e;
    private F f1;
    private F f2;

    private E2F e2f1;
    private E2F e2f2;

    public RelationResultOfTest(XOUnit xoUnit) {
        super(xoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getXOUnits() {
        return xoUnits(E.class, F.class, E2F.class);
    }

    @Before
    public void createData() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction().begin();
        e = xoManager.create(E.class);
        f1 = xoManager.create(F.class);
        e2f1 = xoManager.create(e, E2F.class, f1);
        e2f1.setValue("E2F1");
        f2 = xoManager.create(F.class);
        e2f2 = xoManager.create(e, E2F.class, f2);
        e2f2.setValue("E2F2");
        xoManager.currentTransaction().commit();
    }

    @Test
    public void resultUsingExplicitQuery() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction().begin();
        Result<E2F.ByValue> byValue = e2f1.getResultByValueUsingExplicitQuery("E2F1");
        assertThat(byValue.getSingleResult().getF(), equalTo(f1));
        xoManager.currentTransaction().commit();
    }

    @Test
    public void resultUsingReturnType() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction().begin();
        Result<E2F.ByValue> byValue = e2f1.getResultByValueUsingReturnType("E2F1");
        assertThat(byValue.getSingleResult().getF(), equalTo(f1));
        xoManager.currentTransaction().commit();
    }

    @Test
    public void byValueUsingExplicitQuery() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction().begin();
        E2F.ByValue byValue = e2f1.getByValueUsingExplicitQuery("E2F1");
        assertThat(byValue.getF(), equalTo(f1));
        xoManager.currentTransaction().commit();
    }

    @Test
    public void byValueUsingReturnType() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction().begin();
        E2F.ByValue byValue = e2f1.getByValueUsingReturnType("E2F1");
        assertThat(byValue.getF(), equalTo(f1));
        byValue = e2f1.getByValueUsingReturnType("unknownE2F");
        assertThat(byValue, equalTo(null));
        xoManager.currentTransaction().commit();
    }

    @Test
    public void byValueUsingImplicitThis() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction().begin();
        E2F.ByValueUsingImplicitThis byValue = e2f1.getByValueUsingImplicitThis("E2F1");
        assertThat(byValue.getF(), equalTo(f1));
        xoManager.currentTransaction().commit();
    }

    @Test
    public void resultUsingCypher() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction().begin();
        Result<F> result = e2f1.getResultUsingCypher("E2F1");
        assertThat(result, hasItems(equalTo(f1)));
        result = e2f1.getResultUsingCypher("unknownF");
        assertThat(result.iterator().hasNext(), equalTo(false));
        xoManager.currentTransaction().commit();
    }

    @Test
    public void singleResultUsingCypher() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction().begin();
        F result = e2f1.getSingleResultUsingCypher("E2F1");
        assertThat(result, equalTo(f1));
        result = e2f1.getSingleResultUsingCypher("unknownF");
        assertThat(result, equalTo(null));
        xoManager.currentTransaction().commit();
    }

    @Test
    public void voidResultUsingCypher() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction().begin();
        e2f1.voidResultUsingCypher("E2F1");
        assertThat(xoManager.createQuery("match ()-[e2f:E2F]->() where e2f.result='true' return e2f", E2F.class).execute().getSingleResult(), equalTo(e2f1));
        xoManager.currentTransaction().commit();
    }
}
