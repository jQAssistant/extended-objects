package com.buschmais.xo.inject.test;

import com.buschmais.xo.api.XOManagerFactory;
import com.buschmais.xo.api.bootstrap.XO;
import com.buschmais.xo.impl.XOManagerFactoryImpl;
import com.buschmais.xo.inject.GuiceModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import org.junit.Test;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import static com.google.inject.Guice.createInjector;
import static org.junit.Assert.assertEquals;

public class GuiceXOManagerFactoryInjectionTest {

    private Injector injector = createInjector(
            new GuiceModule("default") {
                @Provides
                @Singleton
                @Named("guice")
                XOManagerFactory special() {
                    return XO.createXOManagerFactory("guice");
                }
            }
    );

    @Test
    public void testInjectedFieldCdoManager() {
        A a = injector.getInstance(A.class);
        assertEquals("guice", ((XOManagerFactoryImpl) a.guiceCdoManager).getXOUnit().getName());
        assertEquals("default", ((XOManagerFactoryImpl) a.defaultCdoManager).getXOUnit().getName());
    }

    @Test
    public void testInjectedConstructorCdoManager() {
        B b = injector.getInstance(B.class);
        assertEquals("guice", ((XOManagerFactoryImpl) b.guiceCdoManager).getXOUnit().getName());
        assertEquals("default", ((XOManagerFactoryImpl) b.defaultCdoManager).getXOUnit().getName());
    }

    static class A {
        @Inject
        @Named("guice")
        XOManagerFactory guiceCdoManager;

        @Inject
        XOManagerFactory defaultCdoManager;
    }

    static class B {

        private final XOManagerFactory guiceCdoManager;
        private final XOManagerFactory defaultCdoManager;

        @Inject
        public B(@Named("guice") XOManagerFactory guiceCdoManager, XOManagerFactory defaultCdoManager) {
            this.guiceCdoManager = guiceCdoManager;
            this.defaultCdoManager = defaultCdoManager;
        }
    }
}
