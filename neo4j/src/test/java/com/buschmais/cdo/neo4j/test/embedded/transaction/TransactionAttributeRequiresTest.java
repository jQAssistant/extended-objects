package com.buschmais.cdo.neo4j.test.embedded.transaction;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.api.bootstrap.CdoUnit;
import com.buschmais.cdo.neo4j.test.embedded.AbstractEmbeddedCdoManagerTest;
import com.buschmais.cdo.neo4j.test.embedded.transaction.composite.A;
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class TransactionAttributeRequiresTest extends AbstractEmbeddedCdoManagerTest {

    @Override
    protected Class<?>[] getTypes() {
        return new Class<?>[]{A.class};
    }

    @Override
    protected CdoUnit.TransactionAttribute getTransactionAttribute() {
        return CdoUnit.TransactionAttribute.REQUIRES;
    }

    @Test
    public void withoutTransactionContext() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        A a = cdoManager.create(A.class);
        a.setValue("value1");
        cdoManager.currentTransaction().commit();
        assertThat(cdoManager.currentTransaction().isActive(), equalTo(false));
        assertThat(a.getValue(), equalTo("value1"));
        a.setValue("value2");
        assertThat(a.getValue(), equalTo("value2"));
    }

    @Test
    public void withTransactionContext() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        A a = cdoManager.create(A.class);
        a.setValue("value1");
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
        A a = cdoManager.create(A.class);
        a.setValue("value1");
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
        A a = cdoManager.create(A.class);
        a.setValue("value1");
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
}
