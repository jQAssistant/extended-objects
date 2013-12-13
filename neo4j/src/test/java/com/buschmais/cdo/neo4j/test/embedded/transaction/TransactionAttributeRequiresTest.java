package com.buschmais.cdo.neo4j.test.embedded.transaction;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.api.TransactionAttribute;
import com.buschmais.cdo.neo4j.test.embedded.transaction.composite.B;
import com.buschmais.cdo.neo4j.test.embedded.AbstractEmbeddedCdoManagerTest;
import com.buschmais.cdo.neo4j.test.embedded.transaction.composite.A;
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class TransactionAttributeRequiresTest extends AbstractEmbeddedCdoManagerTest {

    @Override
    protected Class<?>[] getTypes() {
        return new Class<?>[]{A.class, B.class};
    }

    @Override
    protected TransactionAttribute getTransactionAttribute() {
        return TransactionAttribute.REQUIRES;
    }

    @Test
    public void withoutTransactionContext() {
        CdoManager cdoManager = getCdoManager();
        assertThat(cdoManager.currentTransaction().isActive(), equalTo(false));
        A a = createA(cdoManager);
        assertThat(a.getValue(), equalTo("value1"));
        assertThat(cdoManager.find(A.class, "value1").getSingleResult(), equalTo(a));
        assertThat(((A.ByValue) cdoManager.createQuery(A.ByValue.class).withParameter("value", "value1").execute().getSingleResult()).getA(), equalTo(a));
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
        CdoManager cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        A a = createA(cdoManager);
        cdoManager.currentTransaction().commit();
        cdoManager.currentTransaction().begin();
        assertThat(cdoManager.currentTransaction().isActive(), equalTo(true));
        assertThat(a.getValue(), equalTo("value1"));
        a.setValue("value2");
        cdoManager.currentTransaction().commit();
        cdoManager.currentTransaction().begin();
        assertThat(cdoManager.currentTransaction().isActive(), equalTo(true));
        assertThat(a.getValue(), equalTo("value2"));
        a.setValue("value3");
        cdoManager.currentTransaction().rollback();
        assertThat(a.getValue(), equalTo("value2"));
    }


    @Test
    public void commitOnException() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        A a = createA(cdoManager);
        cdoManager.currentTransaction().commit();
        assertThat(a.getValue(), equalTo("value1"));
        try {
            a.throwException("value2");
            Assert.fail("An Exception is expected.");
        } catch (Exception e) {
        }
        assertThat(cdoManager.currentTransaction().isActive(), equalTo(false));
        assertThat(a.getValue(), equalTo("value2"));
    }

    @Test
    public void rollbackOnRuntimeException() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        A a = createA(cdoManager);
        cdoManager.currentTransaction().commit();
        assertThat(a.getValue(), equalTo("value1"));
        try {
            a.throwRuntimeException("value2");
            Assert.fail("A RuntimeException is expected.");
        } catch (RuntimeException e) {
        }
        assertThat(cdoManager.currentTransaction().isActive(), equalTo(false));
        assertThat(a.getValue(), equalTo("value1"));
    }

    private A createA(CdoManager cdoManager) {
        A a = cdoManager.create(A.class);
        a.setValue("value1");
        B b1 = cdoManager.create(B.class);
        b1.setValue(1);
        a.getListOfB().add(b1);
        B b2 = cdoManager.create(B.class);
        b2.setValue(2);
        a.getListOfB().add(b2);
        return a;
    }

}
