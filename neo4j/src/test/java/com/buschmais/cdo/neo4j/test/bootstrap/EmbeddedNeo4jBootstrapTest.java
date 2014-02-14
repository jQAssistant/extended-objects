package com.buschmais.cdo.neo4j.test.bootstrap;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.api.CdoManagerFactory;
import com.buschmais.cdo.api.bootstrap.Cdo;
import com.buschmais.cdo.neo4j.test.bootstrap.composite.A;
import org.junit.Test;

public class EmbeddedNeo4jBootstrapTest {

    @Test
    public void bootstrap() {
        CdoManagerFactory cdoManagerFactory = Cdo.createCdoManagerFactory("Neo4jEmbedded");
        CdoManager cdoManager = cdoManagerFactory.createCdoManager();
        cdoManager.currentTransaction().begin();
        A a = cdoManager.create(A.class);
        a.setName("Test");
        cdoManager.currentTransaction().commit();
        cdoManagerFactory.close();
    }

}
