package com.buschmais.xo.neo4j.test.transaction;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.buschmais.xo.api.ConcurrencyMode;
import com.buschmais.xo.api.Transaction;
import com.buschmais.xo.api.ValidationMode;
import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractNeo4JXOManagerIT;
import com.buschmais.xo.neo4j.test.Neo4jDatabase;
import com.buschmais.xo.neo4j.test.transaction.composite.A;
import com.buschmais.xo.neo4j.test.transaction.composite.B;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class TransactionAttributeRequiresIT extends AbstractNeo4JXOManagerIT {

    public TransactionAttributeRequiresIT(XOUnit xoUnit) {
        super(xoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getXOUnits() {
        return xoUnits(asList(Neo4jDatabase.MEMORY, Neo4jDatabase.BOLT), asList(A.class, B.class), Collections.emptyList(), ValidationMode.AUTO,
                ConcurrencyMode.SINGLETHREADED, Transaction.TransactionAttribute.REQUIRES);
    }

    @Test
    public void withoutTransactionContext() {
        XOManager xoManager = getXOManager();
        assertThat(xoManager.currentTransaction().isActive()).isFalse();
        A a = createA(xoManager);
        assertThat(a.getValue()).isEqualTo("value1");
        assertThat(xoManager.find(A.class, "value1").getSingleResult()).isEqualTo(a);
        closeXOmanager();
        xoManager = getXOManager();
        a = xoManager.createQuery(A.ByValue.class).withParameter("value", "value1").execute().getSingleResult().getA();
        assertThat(a.getValue()).isEqualTo("value1");
        assertThat(a.getByValue("value1").getA()).isEqualTo(a);
        a.setValue("value2");
        assertThat(a.getValue()).isEqualTo("value2");
        assertThat(a.getListOfB()).hasSize(2);
        List<B> listOfB = new ArrayList<>(a.getListOfB());
        Collections.sort(listOfB, (o1, o2) -> o1.getValue() - o2.getValue());
        int i = 1;
        for (B b : listOfB) {
            assertThat(b.getValue()).isEqualTo(i);
            i++;
        }
    }

    @Test
    public void withTransactionContext() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction().begin();
        A a = createA(xoManager);
        xoManager.currentTransaction().commit();
        xoManager.currentTransaction().begin();
        assertThat(xoManager.currentTransaction().isActive()).isTrue();
        assertThat(a.getValue()).isEqualTo("value1");
        a.setValue("value2");
        xoManager.currentTransaction().commit();
        xoManager.currentTransaction().begin();
        assertThat(xoManager.currentTransaction().isActive()).isTrue();
        assertThat(a.getValue()).isEqualTo("value2");
        a.setValue("value3");
        xoManager.currentTransaction().rollback();
        assertThat(a.getValue()).isEqualTo("value2");
    }

    @Test
    public void commitOnException() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction().begin();
        A a = createA(xoManager);
        xoManager.currentTransaction().commit();
        assertThat(a.getValue()).isEqualTo("value1");
        try {
            a.throwException("value2");
            Assert.fail("An Exception is expected.");
        } catch (Exception e) {
        }
        assertThat(xoManager.currentTransaction().isActive()).isFalse();
        assertThat(a.getValue()).isEqualTo("value2");
    }

    @Test
    public void rollbackOnRuntimeException() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction().begin();
        A a = createA(xoManager);
        xoManager.currentTransaction().commit();
        assertThat(a.getValue()).isEqualTo("value1");
        try {
            a.throwRuntimeException("value2");
            Assert.fail("A RuntimeException is expected.");
        } catch (RuntimeException e) {
        }
        assertThat(xoManager.currentTransaction().isActive()).isFalse();
        assertThat(a.getValue()).isEqualTo("value1");
    }

    private A createA(XOManager xoManager) {
        A a = xoManager.create(A.class);
        a.setValue("value1");
        B b1 = xoManager.create(B.class);
        b1.setValue(1);
        a.getListOfB().add(b1);
        B b2 = xoManager.create(B.class);
        b2.setValue(2);
        a.getListOfB().add(b2);
        return a;
    }

}
