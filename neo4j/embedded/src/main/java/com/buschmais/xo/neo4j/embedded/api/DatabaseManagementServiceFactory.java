package com.buschmais.xo.neo4j.embedded.api;

import java.net.URI;
import java.util.Properties;

import org.neo4j.configuration.Config;
import org.neo4j.dbms.api.DatabaseManagementService;

interface DatabaseManagementServiceFactory {

    DatabaseManagementService createDatabaseManagementService(URI uri, Config config, Properties properties);

}
