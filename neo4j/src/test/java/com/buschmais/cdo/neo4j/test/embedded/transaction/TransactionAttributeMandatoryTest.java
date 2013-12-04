package com.buschmais.cdo.neo4j.test.embedded.transaction;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.api.bootstrap.CdoUnit;
import com.buschmais.cdo.neo4j.test.embedded.AbstractEmbeddedCdoManagerTest;
import com.buschmais.cdo.neo4j.test.embedded.transaction.composite.A;
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class TransactionAttributeMandatoryTest extends AbstractEmbeddedCdoManagerTest {

    @Override
    protected Class<?>[] getTypes() {
        return new Class<?>[]{A.class};
    }

    @Override
    protected CdoUnit.TransactionAttribute getTransactionAttribute() {
        return CdoUnit.TransactionAttribute.MANDATORY;
    }

    @Test
    public void withoutTransactionContext() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        A a = cdoManager.create(A.class);
        a.setValue("value1");
        cdoManager.currentTransaction().commit();
        assertThat(cdoManager.currentTransaction().isActive(), equalTo(false));
        try {
            a.getValue();
            Assert.fail("A CdoException is expected.");
        } catch (CdoException e) {
        }
        try {
            a.setValue("value2");
            Assert.fail("A CdoException is expected.");
        } catch (CdoException e) {
        }
    }
}
