package com.buschmais.xo.neo4j.embedded.api;

import java.net.URI;

import org.neo4j.dbms.api.DatabaseManagementServiceBuilder;

interface DatabaseManagementServiceBuilderFactory {

    DatabaseManagementServiceBuilder createDatabaseManagementServiceBuilder(URI uri);

}
