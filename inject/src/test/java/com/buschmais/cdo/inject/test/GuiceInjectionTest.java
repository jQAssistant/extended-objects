package com.buschmais.cdo.inject.test;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.api.CdoManagerFactory;
import com.buschmais.cdo.inject.CdoContext;
import com.buschmais.cdo.inject.GuiceModule;
import com.buschmais.cdo.inject.test.composite.A;
import com.buschmais.cdo.inject.test.composite.B;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class GuiceInjectionTest {

    @CdoContext(url = "target/neo4j/first", classes = {A.class})
    private CdoManagerFactory cdoManagerFactory1;
    private CdoManager cdoManager1;

    @CdoContext(url = "target/neo4j/second", classes = {B.class})
    private CdoManagerFactory cdoManagerFactory2;
    private CdoManager cdoManager2;

    @Before
    public void setUp() {
        Injector injector = Guice.createInjector(new GuiceModule());
        injector.injectMembers(this);
        cdoManager1 = cdoManagerFactory1.createCdoManager();
        cdoManager2 = cdoManagerFactory2.createCdoManager();
        dropDatabase(cdoManager1);
        dropDatabase(cdoManager2);
    }

    @After
    public void tearDown() {
        closeCdoManager(cdoManager1);
        closeCdoManager(cdoManager2);
        cdoManagerFactory1.close();
        cdoManagerFactory2.close();
    }

    @Test
    public void testInjectedCdoManager() {
        cdoManager1.currentTransaction().begin();
        A a = cdoManager1.create(A.class);
        a.setValue("Google Guice");
        cdoManager1.currentTransaction().commit();

        cdoManager1.currentTransaction().begin();
        cdoManager2.currentTransaction().begin();
        A result = cdoManager1.find(A.class, "Google Guice").getSingleResult();
        B b = cdoManager2.create(B.class);
        b.setValue(result.getValue());
        cdoManager2.currentTransaction().commit();
        cdoManager1.currentTransaction().commit();
    }

    private void closeCdoManager(CdoManager cdoManager) {
        if (cdoManager != null) {
            cdoManager.close();
            cdoManager = null;
        }
    }

    private void dropDatabase(CdoManager cdoManager) {
        cdoManager.currentTransaction().begin();
        cdoManager.createQuery("MATCH (n)-[r]-() DELETE r").execute();
        cdoManager.createQuery("MATCH (n) DELETE n").execute();
        cdoManager.currentTransaction().commit();
    }
}
