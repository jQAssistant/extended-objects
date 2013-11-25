package com.buschmais.cdo.neo4j.api;

import com.buschmais.cdo.api.CdoManager;
import org.neo4j.graphdb.GraphDatabaseService;

public interface Neo4jCdoManager extends CdoManager {

    GraphDatabaseService getGraphDatabaseService();

}
