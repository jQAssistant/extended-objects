package com.buschmais.xo.inject.test;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.XOManagerFactory;
import com.buschmais.xo.inject.GuiceModule;
import com.buschmais.xo.inject.test.composite.A;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;

public class GuiceInjectionTest {

    @Inject
    private XOManagerFactory XOManagerFactory;

    private XOManager XOManager;

    @Before
    public void setUp() {
        Injector injector = Guice.createInjector(new GuiceModule());
        injector.injectMembers(this);
        XOManager = XOManagerFactory.createXOManager();
        dropDatabase();
    }

    @After
    public void tearDown() {
        closeCdoManager();
        XOManagerFactory.close();
    }

    @Test
    public void testInjectedCdoManager() {
        assertThat(XOManagerFactory, notNullValue());
        XOManager.currentTransaction().begin();
        A a = XOManager.create(A.class);
        a.setValue("Google Guice");
        XOManager.currentTransaction().commit();

        XOManager.currentTransaction().begin();
        assertEquals("Google Guice", a.getValue());
        XOManager.currentTransaction().commit();
    }

    private void closeCdoManager() {
        if (XOManager != null) {
            XOManager.close();
            XOManager = null;
        }
    }

    private void dropDatabase() {
        XOManager.currentTransaction().begin();
        XOManager.createQuery("MATCH (n)-[r]-() DELETE r").execute();
        XOManager.createQuery("MATCH (n) DELETE n").execute();
        XOManager.currentTransaction().commit();
    }
}
