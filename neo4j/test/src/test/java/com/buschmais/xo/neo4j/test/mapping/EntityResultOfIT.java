package com.buschmais.xo.neo4j.test.mapping;

import java.util.Collection;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractNeo4JXOManagerIT;
import com.buschmais.xo.neo4j.test.mapping.composite.E;
import com.buschmais.xo.neo4j.test.mapping.composite.E2F;
import com.buschmais.xo.neo4j.test.mapping.composite.F;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static com.buschmais.xo.api.Query.Result;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class EntityResultOfIT extends AbstractNeo4JXOManagerIT {

    private E e;
    private F f1;
    private F f2;

    public EntityResultOfIT(XOUnit xoUnit) {
        super(xoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getXOUnits() {
        return xoUnits(E.class, F.class, E2F.class);
    }

    @Before
    public void createData() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction()
            .begin();
        e = xoManager.create(E.class);
        f1 = xoManager.create(F.class);
        f1.setValue("F1");
        e.getRelatedTo()
            .add(f1);
        f2 = xoManager.create(F.class);
        f2.setValue("F2");
        e.getRelatedTo()
            .add(f2);
        xoManager.currentTransaction()
            .commit();
    }

    @Test
    public void resultUsingExplicitQuery() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction()
            .begin();
        Result<E.ByValue> byValue = e.getResultByValueUsingExplicitQuery("F1");
        assertThat(byValue.getSingleResult()
            .getF()).isEqualTo(f1);
        xoManager.currentTransaction()
            .commit();
    }

    @Test
    public void resultUsingReturnType() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction()
            .begin();
        Result<E.ByValue> byValue = e.getResultByValueUsingReturnType("F1");
        assertThat(byValue.getSingleResult()
            .getF()).isEqualTo(f1);
        xoManager.currentTransaction()
            .commit();
    }

    @Test
    public void byValueUsingExplicitQuery() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction()
            .begin();
        E.ByValue byValue = e.getByValueUsingExplicitQuery("F1");
        assertThat(byValue.getF()).isEqualTo(f1);
        xoManager.currentTransaction()
            .commit();
    }

    @Test
    public void byValueUsingReturnType() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction()
            .begin();
        E.ByValue byValue = e.getByValueUsingReturnType("F1");
        assertThat(byValue.getF()).isEqualTo(f1);
        byValue = e.getByValueUsingReturnType("unknownF");
        assertThat(byValue).isNull();
        xoManager.currentTransaction()
            .commit();
    }

    @Test
    public void byValueUsingImplicitThis() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction()
            .begin();
        E.ByValueUsingImplicitThis byValue = e.getByValueUsingImplicitThis("F1");
        assertThat(byValue.getF()).isEqualTo(f1);
        xoManager.currentTransaction()
            .commit();
    }

    @Test
    public void resultUsingCypher() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction()
            .begin();
        Result<F> result = e.getResultUsingCypher("F1");
        assertThat(result).containsExactly(f1);
        result = e.getResultUsingCypher("unknownF");
        assertThat(result.iterator()
            .hasNext()).isFalse();
        xoManager.currentTransaction()
            .commit();
    }

    @Test
    public void singleResultUsingCypher() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction()
            .begin();
        F result = e.getSingleResultUsingCypher("F1");
        assertThat(result).isEqualTo(f1);
        result = e.getSingleResultUsingCypher("unknownF");
        assertThat(result).isNull();
        xoManager.currentTransaction()
            .commit();
    }

    @Test
    public void voidResultUsingCypher() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction()
            .begin();
        e.voidResultUsingCypher("F1");
        assertThat(xoManager.createQuery("match (e:E) return e", E.class)
            .execute()
            .getSingleResult()).isEqualTo(e);
        xoManager.currentTransaction()
            .commit();
    }

    @Test
    public void overrideParameterName() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction()
            .begin();
        F result = e.overrideParameterName("F1");
        assertThat(result).isEqualTo(f1);
    }

}
