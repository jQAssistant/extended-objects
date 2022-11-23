package com.buschmais.xo.neo4j.embedded.api;

import java.net.URI;

import org.neo4j.dbms.api.DatabaseManagementServiceBuilder;
import org.neo4j.test.TestDatabaseManagementServiceBuilder;

public class MemoryDatabaseManagementServiceBuilderFactory implements DatabaseManagementServiceBuilderFactory {

    @Override
    public DatabaseManagementServiceBuilder createDatabaseManagementServiceBuilder(URI uri) {
        return new TestDatabaseManagementServiceBuilder().impermanent();
    }

}
