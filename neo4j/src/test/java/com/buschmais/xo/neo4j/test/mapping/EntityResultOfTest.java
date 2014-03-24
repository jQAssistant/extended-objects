package com.buschmais.xo.neo4j.test.mapping;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractCdoManagerTest;
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
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class EntityResultOfTest extends AbstractCdoManagerTest {

    private E e;
    private F f1;
    private F f2;

    public EntityResultOfTest(XOUnit XOUnit) {
        super(XOUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getCdoUnits() throws URISyntaxException {
        return cdoUnits(asList(Database.MEMORY), asList(E.class, F.class, E2F.class));
    }

    @Before
    public void createData() {
        XOManager XOManager = getXOManager();
        XOManager.currentTransaction().begin();
        e = XOManager.create(E.class);
        f1 = XOManager.create(F.class);
        f1.setValue("F1");
        e.getRelatedTo().add(f1);
        f2 = XOManager.create(F.class);
        f2.setValue("F2");
        e.getRelatedTo().add(f2);
        XOManager.currentTransaction().commit();
    }

    @Test
    public void resultUsingExplicitQuery() {
        XOManager XOManager = getXOManager();
        XOManager.currentTransaction().begin();
        Result<E.ByValue> byValue = e.getResultByValueUsingExplicitQuery("F1");
        assertThat(byValue.getSingleResult().getF(), equalTo(f1));
        XOManager.currentTransaction().commit();
    }

    @Test
    public void resultUsingReturnType() {
        XOManager XOManager = getXOManager();
        XOManager.currentTransaction().begin();
        Result<E.ByValue> byValue = e.getResultByValueUsingReturnType("F1");
        assertThat(byValue.getSingleResult().getF(), equalTo(f1));
        XOManager.currentTransaction().commit();
    }

    @Test
    public void byValueUsingExplicitQuery() {
        XOManager XOManager = getXOManager();
        XOManager.currentTransaction().begin();
        E.ByValue byValue = e.getByValueUsingExplicitQuery("F1");
        assertThat(byValue.getF(), equalTo(f1));
        XOManager.currentTransaction().commit();
    }

    @Test
    public void byValueUsingReturnType() {
        XOManager XOManager = getXOManager();
        XOManager.currentTransaction().begin();
        E.ByValue byValue = e.getByValueUsingReturnType("F1");
        assertThat(byValue.getF(), equalTo(f1));
        byValue = e.getByValueUsingReturnType("unknownF");
        assertThat(byValue, equalTo(null));
        XOManager.currentTransaction().commit();
    }

    @Test
    public void byValueUsingImplicitThis() {
        XOManager XOManager = getXOManager();
        XOManager.currentTransaction().begin();
        E.ByValueUsingImplicitThis byValue = e.getByValueUsingImplicitThis("F1");
        assertThat(byValue.getF(), equalTo(f1));
        XOManager.currentTransaction().commit();
    }

    @Test
    public void resultUsingCypher() {
        XOManager XOManager = getXOManager();
        XOManager.currentTransaction().begin();
        Result<F> result = e.getResultUsingCypher("F1");
        assertThat(result, hasItems(equalTo(f1)));
        result = e.getResultUsingCypher("unknownF");
        assertThat(result.iterator().hasNext(), equalTo(false));
        XOManager.currentTransaction().commit();
    }

    @Test
    public void singleResultUsingCypher() {
        XOManager XOManager = getXOManager();
        XOManager.currentTransaction().begin();
        F result = e.getSingleResultUsingCypher("F1");
        assertThat(result, equalTo(f1));
        result = e.getSingleResultUsingCypher("unknownF");
        assertThat(result, equalTo(null));
        XOManager.currentTransaction().commit();
    }
}
