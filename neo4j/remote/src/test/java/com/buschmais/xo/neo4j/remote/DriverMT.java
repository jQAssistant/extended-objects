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
        XOUnit xoUnit = XOUnit.builder().provider(Neo4jRemoteStoreProvider.class).uri(new URI("bolt://localhost:7687")).properties(properties)
                .type(Person.class).type(Customer.class).build();
        XOManagerFactory xoManagerFactory = XO.createXOManagerFactory(xoUnit);
        XOManager xoManager1 = xoManagerFactory.createXOManager();
        xoManager1.currentTransaction().begin();
        Person person1 = xoManager1.create((exampe) -> {
            exampe.setName("Foo");
        }, Person.class);
        Person person2 = xoManager1.create(Person.class);
        person2.setName("Bar");
        xoManager1.currentTransaction().commit();
        XOManager xoManager2 = xoManagerFactory.createXOManager();
        xoManager2.currentTransaction().begin();
        Person byId1 = xoManager1.findById(Person.class, person1.getId());
        byId1.setName("FOO");
        Person byId2 = xoManager2.findById(Person.class, person2.getId());
        byId2.setName("BAR");
        Customer customer = xoManager2.migrate(byId2).add(Customer.class).as(Customer.class);
        customer.setCustomerNo(12345);
        Person p = xoManager2.migrate(customer).remove(Customer.class).as(Person.class);
        for (Person foo : xoManager2.find(Person.class, "BAR")) {
            System.out.println(foo);
        }
        xoManager2.currentTransaction().commit();
        xoManager2.close();
        xoManager1.currentTransaction().begin();
        xoManager1.delete(person1);
        xoManager1.delete(person2);
        xoManager1.currentTransaction().commit();
        xoManager1.close();
    }
}
