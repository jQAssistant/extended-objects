package com.buschmais.xo.neo4j.test.transaction;

import com.buschmais.xo.api.*;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractCdoManagerTest;
import com.buschmais.xo.neo4j.test.transaction.composite.A;
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

    public TransactionAttributeMandatoryTest(XOUnit XOUnit) {
        super(XOUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getCdoUnits() throws URISyntaxException {
        return cdoUnits(asList(Database.MEMORY), Arrays.asList(A.class), Collections.<Class<?>>emptyList(), ValidationMode.AUTO, ConcurrencyMode.SINGLETHREADED, Transaction.TransactionAttribute.MANDATORY);
    }

    @Test
    public void withoutTransactionContext() {
        XOManager XOManager = getXOManager();
        XOManager.currentTransaction().begin();
        A a = XOManager.create(A.class);
        a.setValue("value1");
        XOManager.currentTransaction().commit();
        assertThat(XOManager.currentTransaction().isActive(), equalTo(false));
        try {
            a.getValue();
            Assert.fail("A XOException is expected.");
        } catch (XOException e) {
        }
        try {
            a.setValue("value2");
            Assert.fail("A XOException is expected.");
        } catch (XOException e) {
        }
    }
}
