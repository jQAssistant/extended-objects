package com.buschmais.xo.neo4j.embedded.api;

import java.nio.file.Path;

import lombok.extern.slf4j.Slf4j;
import org.neo4j.dbms.api.DatabaseManagementServiceBuilder;
import org.neo4j.dbms.api.Neo4jDatabaseManagementServiceBuilder;

@Slf4j
public class FileDatabaseManagementServiceFactory extends AbstractEmbeddedDatabaseManagementServiceFactory {

    @Override
    protected Neo4jDatabaseManagementServiceBuilder getDatabaseManagementServiceBuilder(Path directory) {
        return new DatabaseManagementServiceBuilder(directory);
    }

}
