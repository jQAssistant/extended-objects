package com.buschmais.cdo.neo4j.test.embedded.bootstrap;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.api.CdoManagerFactory;
import com.buschmais.cdo.api.bootstrap.Cdo;
import com.buschmais.cdo.api.bootstrap.CdoProvider;
import com.buschmais.cdo.neo4j.test.embedded.bootstrap.composite.A;
import org.junit.Test;

public class EmbeddedNeo4jBootstrapTest {

    @Test
    public void bootstrap() {
        CdoManagerFactory cdoManagerFactory = Cdo.createCdoManagerFactory("Neo4jEmbedded");
        CdoManager cdoManager = cdoManagerFactory.createCdoManager();
        cdoManager.begin();
        A a = cdoManager.create(A.class);
        a.setName("Test");
        cdoManager.commit();
        cdoManagerFactory.close();
    }

}
