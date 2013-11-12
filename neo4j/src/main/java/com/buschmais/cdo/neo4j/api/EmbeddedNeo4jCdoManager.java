package com.buschmais.cdo.neo4j.api;

import com.buschmais.cdo.api.CdoManager;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.kernel.GraphDatabaseAPI;

public interface EmbeddedNeo4jCdoManager extends CdoManager {

    GraphDatabaseService getGraphDatabaseService();

}
