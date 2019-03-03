package com.buschmais.xo.neo4j.doc.bootstrap;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.XOManagerFactory;
import com.buschmais.xo.api.bootstrap.XO;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.embedded.api.EmbeddedNeo4jXOProvider;

import org.junit.Test;

public class BootstrapTest {

    private class XOUnitClassLoader extends ClassLoader {

        private ClassLoader delegate;

        private String resourceRoot;

        private XOUnitClassLoader(ClassLoader delegate, String resourceRoot) {
            this.delegate = delegate;
            this.resourceRoot = resourceRoot;
        }

        @Override
        public Enumeration<URL> getResources(String name) throws IOException {
            return delegate.getResources(getName(name));
        }

        @Override
        public URL getResource(String name) {
            return delegate.getResource(getName(name));
        }

        @Override
        public InputStream getResourceAsStream(String name) {
            return delegate.getResourceAsStream(getName(name));
        }

        private String getName(String name) {
            if ("META-INF/xo.xml".equals(name)) {
                return resourceRoot + "/" + name;
            }
            return name;
        }

    }

    @Test
    public void bootstrapXml() {
        XOUnitClassLoader XOUnitClassLoader = new XOUnitClassLoader(BootstrapTest.class.getClassLoader(), "embedded");
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(XOUnitClassLoader);
        // tag::BootstrapXml[]
        XOManagerFactory movies = XO.createXOManagerFactory("movies");
        XOManager xoManager = movies.createXOManager();

        xoManager.currentTransaction().begin();

        Person person = xoManager.create(Person.class);
        person.setName("Indiana Jones");

        xoManager.currentTransaction().commit();

        xoManager.close();
        movies.close();
        // end::BootstrapXml[]
        Thread.currentThread().setContextClassLoader(contextClassLoader);
    }

    @Test
    public void bootstrapJava() throws URISyntaxException {
        // tag::BootstrapJava[]
        XOUnit xoUnit = XOUnit.builder().provider(EmbeddedNeo4jXOProvider.class).uri(new URI("file:databases/movies")).type(Person.class).type(Actor.class)
                .build();
        XOManagerFactory movies = XO.createXOManagerFactory(xoUnit);
        XOManager xoManager = movies.createXOManager();

        xoManager.currentTransaction().begin();

        Person person = xoManager.create(Person.class);
        person.setName("Indiana Jones");

        xoManager.currentTransaction().commit();

        xoManager.close();
        movies.close();
        // end::BootstrapJava[]
    }

}
