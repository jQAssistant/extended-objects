package com.buschmais.xo.neo4j.test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.buschmais.xo.api.ConcurrencyMode;
import com.buschmais.xo.api.Transaction;
import com.buschmais.xo.api.ValidationMode;
import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.test.AbstractXOManagerTest;

public abstract class AbstractNeo4jXOManagerTest extends AbstractXOManagerTest {

    protected AbstractNeo4jXOManagerTest(XOUnit xoUnit) {
        super(xoUnit);
    }

    protected static Collection<Object[]> xoUnits(Class<?>... types) {
        return xoUnits(Arrays.asList(Neo4jDatabase.MEMORY), Arrays.asList(types), Collections.emptyList(), ValidationMode.AUTO,
                ConcurrencyMode.SINGLETHREADED, Transaction.TransactionAttribute.NONE);
    }

    protected static Collection<Object[]> xoUnits(List<? extends Class<?>> types, List<? extends Class<?>> instanceListeners, ValidationMode validationMode,
            ConcurrencyMode concurrencyMode, Transaction.TransactionAttribute transactionAttribute) {
        return xoUnits(Arrays.asList(Neo4jDatabase.MEMORY), types, instanceListeners, validationMode, concurrencyMode, transactionAttribute);
    }

    protected void dropDatabase() {
        XOManager manager = getXoManager();
        manager.currentTransaction().begin();
        manager.createQuery("MATCH (n)-[r]-() DELETE r,n").execute();
        manager.currentTransaction().commit();
    }
}
