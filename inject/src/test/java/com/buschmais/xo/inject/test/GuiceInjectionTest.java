package com.buschmais.xo.inject.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;

import javax.inject.Inject;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.XOManagerFactory;
import com.buschmais.xo.inject.GuiceModule;
import com.buschmais.xo.inject.test.composite.A;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class GuiceInjectionTest {

    @Inject
    private XOManagerFactory xoManagerFactory;

    private XOManager xoManager;

    @Before
    public void setUp() {
        Injector injector = Guice.createInjector(new GuiceModule());
        injector.injectMembers(this);
        xoManager = xoManagerFactory.createXOManager();
        dropDatabase();
    }

    @After
    public void tearDown() {
        closeXOManager();
        xoManagerFactory.close();
    }

    @Test
    public void testInjectedXOManager() {
        assertThat(xoManagerFactory, notNullValue());
        xoManager.currentTransaction().begin();
        A a = xoManager.create(A.class);
        a.setValue("Google Guice");
        xoManager.currentTransaction().commit();

        xoManager.currentTransaction().begin();
        assertEquals("Google Guice", a.getValue());
        xoManager.currentTransaction().commit();
    }

    private void closeXOManager() {
        if (xoManager != null) {
            xoManager.close();
            xoManager = null;
        }
    }

    private void dropDatabase() {
        xoManager.currentTransaction().begin();
        xoManager.createQuery("MATCH (n)-[r]-() DELETE r").execute();
        xoManager.createQuery("MATCH (n) DELETE n").execute();
        xoManager.currentTransaction().commit();
    }
}
