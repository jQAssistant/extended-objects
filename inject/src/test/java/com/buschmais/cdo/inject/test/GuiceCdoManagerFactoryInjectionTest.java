package com.buschmais.cdo.inject.test;

import com.buschmais.cdo.api.CdoManagerFactory;
import com.buschmais.cdo.api.bootstrap.Cdo;
import com.buschmais.cdo.impl.CdoManagerFactoryImpl;
import com.buschmais.cdo.inject.GuiceModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import org.junit.Test;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import static com.google.inject.Guice.createInjector;
import static org.junit.Assert.assertEquals;

public class GuiceCdoManagerFactoryInjectionTest {

    private Injector injector = createInjector(
            new GuiceModule("default") {
                @Provides @Singleton @Named("guice") CdoManagerFactory special() {
                    return Cdo.createCdoManagerFactory("guice");
                }
            }
    );

    @Test
    public void testInjectedFieldCdoManager() {
        A a = injector.getInstance(A.class);
        assertEquals("guice", ((CdoManagerFactoryImpl) a.guiceCdoManager).getCdoUnit().getName());
        assertEquals("default", ((CdoManagerFactoryImpl) a.defaultCdoManager).getCdoUnit().getName());
    }

    @Test
    public void testInjectedConstructorCdoManager() {
        B b = injector.getInstance(B.class);
        assertEquals("guice", ((CdoManagerFactoryImpl) b.guiceCdoManager).getCdoUnit().getName());
        assertEquals("default", ((CdoManagerFactoryImpl) b.defaultCdoManager).getCdoUnit().getName());
    }

    static class A {
        @Inject @Named("guice")
        CdoManagerFactory guiceCdoManager;

        @Inject
        CdoManagerFactory defaultCdoManager;
    }

    static class B {

        private final CdoManagerFactory guiceCdoManager;
        private final CdoManagerFactory defaultCdoManager;
        @Inject
        public B(@Named("guice") CdoManagerFactory guiceCdoManager, CdoManagerFactory defaultCdoManager) {
            this.guiceCdoManager = guiceCdoManager;
            this.defaultCdoManager = defaultCdoManager;
        }
    }
}
