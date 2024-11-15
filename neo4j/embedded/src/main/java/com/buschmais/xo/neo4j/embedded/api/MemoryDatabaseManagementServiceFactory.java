package com.buschmais.xo.neo4j.embedded.api;

import java.nio.file.Path;

import org.neo4j.dbms.api.Neo4jDatabaseManagementServiceBuilder;
import org.neo4j.test.TestDatabaseManagementServiceBuilder;

public class MemoryDatabaseManagementServiceFactory extends AbstractEmbeddedDatabaseManagementServiceFactory {

    @Override
    protected Neo4jDatabaseManagementServiceBuilder getDatabaseManagementServiceBuilder(Path directory) {
        return new TestDatabaseManagementServiceBuilder(directory).impermanent();
    }

}
