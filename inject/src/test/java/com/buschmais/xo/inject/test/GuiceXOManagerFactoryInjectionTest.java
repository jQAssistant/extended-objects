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
    public void testInjectedFieldXOManager() {
        A a = injector.getInstance(A.class);
        assertEquals("guice", ((XOManagerFactoryImpl) a.guiceXOManager).getXOUnit().getName());
        assertEquals("default", ((XOManagerFactoryImpl) a.defaultXOManager).getXOUnit().getName());
    }

    @Test
    public void testInjectedConstructorXOManager() {
        B b = injector.getInstance(B.class);
        assertEquals("guice", ((XOManagerFactoryImpl) b.guiceXOManager).getXOUnit().getName());
        assertEquals("default", ((XOManagerFactoryImpl) b.defaultXOManager).getXOUnit().getName());
    }

    static class A {
        @Inject
        @Named("guice")
        XOManagerFactory guiceXOManager;

        @Inject
        XOManagerFactory defaultXOManager;
    }

    static class B {

        private final XOManagerFactory guiceXOManager;
        private final XOManagerFactory defaultXOManager;

        @Inject
        public B(@Named("guice") XOManagerFactory guiceXOManager, XOManagerFactory defaultXOManager) {
            this.guiceXOManager = guiceXOManager;
            this.defaultXOManager = defaultXOManager;
        }
    }
}
