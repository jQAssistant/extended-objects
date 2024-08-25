package com.buschmais.xo.neo4j.test.batchable;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assume.assumeThat;

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
        assumeThat(getXOManagerFactory().getXOUnit()
            .getProvider(), equalTo(Neo4jDatabase.BOLT.getProvider()));
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction()
            .begin();
        A a = xoManager.create(A.class);
        a.setName("A1");
        assertThat((Long) xoManager.getId(a)).isNegative();
        B b = xoManager.create(B.class);
        b.setName("B");
        assertThat((Long) xoManager.getId(b)).isNegative();
        A2B a2b = xoManager.create(a, A2B.class, b);
        a2b.setValue(1);
        assertThat((Long) xoManager.getId(a2b)).isNegative();
        xoManager.currentTransaction()
            .commit();
        xoManager.currentTransaction()
            .begin();
        assertThat((Long) xoManager.getId(a)).isNotNegative();
        assertThat((Long) xoManager.getId(a2b)).isNotNegative();
        assertThat((Long) xoManager.getId(b)).isNotNegative();
        assertThat(a.getName()).isEqualTo("A1");
        assertThat(b.getName()).isEqualTo("B");
        assertThat(a2b.getValue()).isEqualTo(1);
        a.setName("A2");
        a2b.setValue(2);
        xoManager.currentTransaction()
            .commit();
        xoManager.currentTransaction()
            .begin();
        a = xoManager.find(A.class, "A2")
            .getSingleResult();
        a2b = a.getA2B();
        assertThat(a2b.getValue()).isEqualTo(2);
        b = xoManager.find(B.class, "B")
            .getSingleResult();
        assertThat(b.getA2B()).isEqualTo(a2b);
        xoManager.currentTransaction()
            .commit();
    }

}
