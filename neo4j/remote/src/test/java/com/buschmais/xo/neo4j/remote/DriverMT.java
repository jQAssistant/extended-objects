package com.buschmais.xo.neo4j.remote;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import org.junit.Test;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.XOManagerFactory;
import com.buschmais.xo.api.bootstrap.XO;
import com.buschmais.xo.api.bootstrap.XOUnit;

public class DriverMT {

    @Test
    public void test() throws URISyntaxException {
        Properties properties = new Properties();
        properties.setProperty("neo4j.remote.username", "neo4j");
        properties.setProperty("neo4j.remote.password", "admin");
        XOUnit xoUnit = XOUnit.builder().provider(Neo4jRemoteStoreProvider.class).uri(new URI("bolt://localhost:17687")).properties(properties)
                .type(Person.class).build();
        XOManagerFactory xoManagerFactory = XO.createXOManagerFactory(xoUnit);
        XOManager xoManager = xoManagerFactory.createXOManager();
        Person person = xoManager.create(Person.class);
        xoManager.close();
    }
}
