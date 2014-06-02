package com.buschmais.xo.json.test.bootstrap;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.XOManagerFactory;
import com.buschmais.xo.api.bootstrap.XO;
import com.buschmais.xo.json.test.bootstrap.composite.A;
import org.junit.Test;

public class JsonFileStoreBootstrapTest {

    @Test
    public void bootstrap() {
        XOManagerFactory xoManagerFactory = XO.createXOManagerFactory("JsonFileStore");
        XOManager xoManager = xoManagerFactory.createXOManager();
        A a = xoManager.create(A.class);
        a.setName("Test");
        xoManager.flush();
        xoManagerFactory.close();
    }

}
