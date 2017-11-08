package com.buschmais.xo.neo4j.test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.ClassRule;
import org.neo4j.harness.junit.Neo4jRule;

import com.buschmais.xo.api.ConcurrencyMode;
import com.buschmais.xo.api.Transaction;
import com.buschmais.xo.api.ValidationMode;
import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.test.AbstractXOManagerTest;

public abstract class AbstractNeo4jXOManagerTest extends AbstractXOManagerTest {

    // This rule starts a Neo4j instance
    @ClassRule
    public static Neo4jRule neo4j = new Neo4jRule().withConfig("dbms.connector.bolt.listen_address","localhost:5001");

    protected AbstractNeo4jXOManagerTest(XOUnit xoUnit) {
        super(xoUnit);
    }

    protected static Collection<Object[]> xoUnits(Class<?>... types) {
        return xoUnits(Arrays.asList(Neo4jDatabase.MEMORY, Neo4jDatabase.BOLT), Arrays.asList(types), Collections.emptyList(), ValidationMode.AUTO,
                ConcurrencyMode.SINGLETHREADED, Transaction.TransactionAttribute.NONE);
    }

    protected static Collection<Object[]> xoUnits(List<? extends Class<?>> types, List<? extends Class<?>> instanceListeners, ValidationMode validationMode,
            ConcurrencyMode concurrencyMode, Transaction.TransactionAttribute transactionAttribute) {
        return xoUnits(Arrays.asList(Neo4jDatabase.MEMORY, Neo4jDatabase.BOLT), types, instanceListeners, validationMode, concurrencyMode,
                transactionAttribute);
    }

    protected void dropDatabase() {
        XOManager manager = getXOManager();
        manager.currentTransaction().begin();
        manager.createQuery("MATCH (n) DETACH DELETE n").execute();
        manager.currentTransaction().commit();
    }
}
