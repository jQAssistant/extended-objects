package com.buschmais.xo.neo4j.test.transaction;

import com.buschmais.xo.api.ConcurrencyMode;
import com.buschmais.xo.api.Transaction;
import com.buschmais.xo.api.ValidationMode;
import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractCdoManagerTest;
import com.buschmais.xo.neo4j.test.transaction.composite.A;
import com.buschmais.xo.neo4j.test.transaction.composite.B;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class TransactionAttributeRequiresTest extends AbstractCdoManagerTest {

    public TransactionAttributeRequiresTest(XOUnit XOUnit) {
        super(XOUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getCdoUnits() throws URISyntaxException {
        return cdoUnits(asList(Database.MEMORY), asList(A.class, B.class), Collections.<Class<?>>emptyList(), ValidationMode.AUTO, ConcurrencyMode.SINGLETHREADED, Transaction.TransactionAttribute.REQUIRES);
    }

    @Test
    public void withoutTransactionContext() {
        XOManager XOManager = getXOManager();
        assertThat(XOManager.currentTransaction().isActive(), equalTo(false));
        A a = createA(XOManager);
        assertThat(a.getValue(), equalTo("value1"));
        assertThat(XOManager.find(A.class, "value1").getSingleResult(), equalTo(a));
        assertThat(XOManager.createQuery(A.ByValue.class).withParameter("value", "value1").execute().getSingleResult().getA(), equalTo(a));
        assertThat(a.getByValue("value1").getA(), equalTo(a));
        a.setValue("value2");
        assertThat(a.getValue(), equalTo("value2"));
        assertThat(a.getListOfB().size(), equalTo(2));
        int i = 1;
        for (B b : a.getListOfB()) {
            assertThat(b.getValue(), equalTo(i));
            i++;
        }
    }

    @Test
    public void withTransactionContext() {
        XOManager XOManager = getXOManager();
        XOManager.currentTransaction().begin();
        A a = createA(XOManager);
        XOManager.currentTransaction().commit();
        XOManager.currentTransaction().begin();
        assertThat(XOManager.currentTransaction().isActive(), equalTo(true));
        assertThat(a.getValue(), equalTo("value1"));
        a.setValue("value2");
        XOManager.currentTransaction().commit();
        XOManager.currentTransaction().begin();
        assertThat(XOManager.currentTransaction().isActive(), equalTo(true));
        assertThat(a.getValue(), equalTo("value2"));
        a.setValue("value3");
        XOManager.currentTransaction().rollback();
        assertThat(a.getValue(), equalTo("value2"));
    }


    @Test
    public void commitOnException() {
        XOManager XOManager = getXOManager();
        XOManager.currentTransaction().begin();
        A a = createA(XOManager);
        XOManager.currentTransaction().commit();
        assertThat(a.getValue(), equalTo("value1"));
        try {
            a.throwException("value2");
            Assert.fail("An Exception is expected.");
        } catch (Exception e) {
        }
        assertThat(XOManager.currentTransaction().isActive(), equalTo(false));
        assertThat(a.getValue(), equalTo("value2"));
    }

    @Test
    public void rollbackOnRuntimeException() {
        XOManager XOManager = getXOManager();
        XOManager.currentTransaction().begin();
        A a = createA(XOManager);
        XOManager.currentTransaction().commit();
        assertThat(a.getValue(), equalTo("value1"));
        try {
            a.throwRuntimeException("value2");
            Assert.fail("A RuntimeException is expected.");
        } catch (RuntimeException e) {
        }
        assertThat(XOManager.currentTransaction().isActive(), equalTo(false));
        assertThat(a.getValue(), equalTo("value1"));
    }

    private A createA(XOManager XOManager) {
        A a = XOManager.create(A.class);
        a.setValue("value1");
        B b1 = XOManager.create(B.class);
        b1.setValue(1);
        a.getListOfB().add(b1);
        B b2 = XOManager.create(B.class);
        b2.setValue(2);
        a.getListOfB().add(b2);
        return a;
    }

}
