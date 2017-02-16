package com.buschmais.xo.neo4j.test.transaction;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.buschmais.xo.api.*;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractNeo4jXOManagerTest;
import com.buschmais.xo.neo4j.test.Neo4jDatabase;
import com.buschmais.xo.neo4j.test.transaction.composite.A;

@RunWith(Parameterized.class)
public class TransactionAttributeMandatoryTest extends AbstractNeo4jXOManagerTest {

    public TransactionAttributeMandatoryTest(XOUnit xoUnit) {
        super(xoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getXOUnits() throws URISyntaxException {
        return xoUnits(asList(Neo4jDatabase.MEMORY, Neo4jDatabase.BOLT), Arrays.asList(A.class), Collections.<Class<?>>emptyList(), ValidationMode.AUTO, ConcurrencyMode.SINGLETHREADED, Transaction.TransactionAttribute.MANDATORY);
    }

    @Test
    public void withoutTransactionContext() {
        XOManager xoManager = getXoManager();
        xoManager.currentTransaction().begin();
        A a = xoManager.create(A.class);
        a.setValue("value1");
        xoManager.currentTransaction().commit();
        assertThat(xoManager.currentTransaction().isActive(), equalTo(false));
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
