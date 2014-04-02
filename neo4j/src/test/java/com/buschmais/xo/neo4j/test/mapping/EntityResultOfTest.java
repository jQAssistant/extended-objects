package com.buschmais.xo.neo4j.test.mapping;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractXOManagerTest;
import com.buschmais.xo.neo4j.test.mapping.composite.E;
import com.buschmais.xo.neo4j.test.mapping.composite.E2F;
import com.buschmais.xo.neo4j.test.mapping.composite.F;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.net.URISyntaxException;
import java.util.Collection;

import static com.buschmais.xo.api.Query.Result;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class EntityResultOfTest extends AbstractXOManagerTest {

    private E e;
    private F f1;
    private F f2;

    public EntityResultOfTest(XOUnit xoUnit) {
        super(xoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getXOUnits() throws URISyntaxException {
        return xoUnits(E.class, F.class, E2F.class);
    }

    @Before
    public void createData() {
        XOManager xoManager = getXoManager();
        xoManager.currentTransaction().begin();
        e = xoManager.create(E.class);
        f1 = xoManager.create(F.class);
        f1.setValue("F1");
        e.getRelatedTo().add(f1);
        f2 = xoManager.create(F.class);
        f2.setValue("F2");
        e.getRelatedTo().add(f2);
        xoManager.currentTransaction().commit();
    }

    @Test
    public void resultUsingExplicitQuery() {
        XOManager xoManager = getXoManager();
        xoManager.currentTransaction().begin();
        Result<E.ByValue> byValue = e.getResultByValueUsingExplicitQuery("F1");
        assertThat(byValue.getSingleResult().getF(), equalTo(f1));
        xoManager.currentTransaction().commit();
    }

    @Test
    public void resultUsingReturnType() {
        XOManager xoManager = getXoManager();
        xoManager.currentTransaction().begin();
        Result<E.ByValue> byValue = e.getResultByValueUsingReturnType("F1");
        assertThat(byValue.getSingleResult().getF(), equalTo(f1));
        xoManager.currentTransaction().commit();
    }

    @Test
    public void byValueUsingExplicitQuery() {
        XOManager xoManager = getXoManager();
        xoManager.currentTransaction().begin();
        E.ByValue byValue = e.getByValueUsingExplicitQuery("F1");
        assertThat(byValue.getF(), equalTo(f1));
        xoManager.currentTransaction().commit();
    }

    @Test
    public void byValueUsingReturnType() {
        XOManager xoManager = getXoManager();
        xoManager.currentTransaction().begin();
        E.ByValue byValue = e.getByValueUsingReturnType("F1");
        assertThat(byValue.getF(), equalTo(f1));
        byValue = e.getByValueUsingReturnType("unknownF");
        assertThat(byValue, equalTo(null));
        xoManager.currentTransaction().commit();
    }

    @Test
    public void byValueUsingImplicitThis() {
        XOManager xoManager = getXoManager();
        xoManager.currentTransaction().begin();
        E.ByValueUsingImplicitThis byValue = e.getByValueUsingImplicitThis("F1");
        assertThat(byValue.getF(), equalTo(f1));
        xoManager.currentTransaction().commit();
    }

    @Test
    public void resultUsingCypher() {
        XOManager xoManager = getXoManager();
        xoManager.currentTransaction().begin();
        Result<F> result = e.getResultUsingCypher("F1");
        assertThat(result, hasItems(equalTo(f1)));
        result = e.getResultUsingCypher("unknownF");
        assertThat(result.iterator().hasNext(), equalTo(false));
        xoManager.currentTransaction().commit();
    }

    @Test
    public void singleResultUsingCypher() {
        XOManager xoManager = getXoManager();
        xoManager.currentTransaction().begin();
        F result = e.getSingleResultUsingCypher("F1");
        assertThat(result, equalTo(f1));
        result = e.getSingleResultUsingCypher("unknownF");
        assertThat(result, equalTo(null));
        xoManager.currentTransaction().commit();
    }
}
