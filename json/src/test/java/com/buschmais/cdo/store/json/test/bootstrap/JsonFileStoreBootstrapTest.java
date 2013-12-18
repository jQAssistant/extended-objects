package com.buschmais.cdo.store.json.test.bootstrap;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.api.CdoManagerFactory;
import com.buschmais.cdo.api.bootstrap.Cdo;
import com.buschmais.cdo.store.json.test.bootstrap.composite.A;
import org.junit.Ignore;
import org.junit.Test;

public class JsonFileStoreBootstrapTest {

    @Test
    public void bootstrap() {
        CdoManagerFactory cdoManagerFactory = Cdo.createCdoManagerFactory("JsonFileStore");
        CdoManager cdoManager = cdoManagerFactory.createCdoManager();
        cdoManager.currentTransaction().begin();
        A a = cdoManager.create(A.class);
        a.setName("Test");
        cdoManager.currentTransaction().commit();
        cdoManagerFactory.close();
    }

}
