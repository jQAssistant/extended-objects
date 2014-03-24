package com.buschmais.xo.json.test.bootstrap;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.XOManagerFactory;
import com.buschmais.xo.api.bootstrap.XO;
import com.buschmais.xo.json.test.bootstrap.composite.A;
import org.junit.Test;

public class JsonFileStoreBootstrapTest {

    @Test
    public void bootstrap() {
        XOManagerFactory XOManagerFactory = XO.createXOManagerFactory("JsonFileStore");
        XOManager xoManager = XOManagerFactory.createXOManager();
        xoManager.currentTransaction().begin();
        A a = xoManager.create(A.class);
        a.setName("Test");
        xoManager.currentTransaction().commit();
        XOManagerFactory.close();
    }

}
