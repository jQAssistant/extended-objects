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
import com.buschmais.xo.test.AbstractXOManagerIT;

import org.junit.ClassRule;
import org.neo4j.configuration.connectors.BoltConnector.EncryptionLevel;
import org.neo4j.configuration.helpers.SocketAddress;
import org.neo4j.harness.junit.rule.Neo4jRule;

import static org.neo4j.configuration.GraphDatabaseInternalSettings.netty_server_shutdown_quiet_period;
import static org.neo4j.configuration.connectors.BoltConnector.encryption_level;
import static org.neo4j.configuration.connectors.BoltConnector.listen_address;

public abstract class AbstractNeo4JXOManagerIT extends AbstractXOManagerIT {

    // This rule starts a Neo4j instance
    @ClassRule
    public static Neo4jRule neo4j = new Neo4jRule().withConfig(listen_address, new SocketAddress("localhost", 6001))
        .withConfig(encryption_level, EncryptionLevel.OPTIONAL)
        .withConfig(netty_server_shutdown_quiet_period, 0);

    protected AbstractNeo4JXOManagerIT(XOUnit xoUnit) {
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
        manager.currentTransaction()
            .begin();
        manager.createQuery("MATCH (n) DETACH DELETE n")
            .execute();
        manager.currentTransaction()
            .commit();
    }
}
