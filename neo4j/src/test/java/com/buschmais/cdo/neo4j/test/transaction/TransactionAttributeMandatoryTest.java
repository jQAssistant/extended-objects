package com.buschmais.cdo.neo4j.test.transaction;

import com.buschmais.cdo.api.*;
import com.buschmais.cdo.api.bootstrap.CdoUnit;
import com.buschmais.cdo.neo4j.test.AbstractCdoManagerTest;
import com.buschmais.cdo.neo4j.test.transaction.composite.A;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class TransactionAttributeMandatoryTest extends AbstractCdoManagerTest {

    public TransactionAttributeMandatoryTest(CdoUnit cdoUnit) {
        super(cdoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getCdoUnits() throws URISyntaxException {
        return cdoUnits(asList(Database.MEMORY), Arrays.asList(A.class), Collections.<Class<?>>emptyList(), ValidationMode.AUTO, ConcurrencyMode.SINGLETHREADED, Transaction.TransactionAttribute.MANDATORY);
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
