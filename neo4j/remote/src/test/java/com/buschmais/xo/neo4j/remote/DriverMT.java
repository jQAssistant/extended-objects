package com.buschmais.xo.neo4j.remote;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.Test;

import com.buschmais.xo.api.ResultIterable;
import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.XOManagerFactory;
import com.buschmais.xo.api.bootstrap.XO;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.remote.api.Neo4jRemoteStoreProvider;

public class DriverMT {

    @Test
    public void test() throws URISyntaxException {
        XOUnit xoUnit = getXoUnit();
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
        Address address = xoManager2.create(Address.class);
        address.setCity("Dresden");
        p.getAddresses().add(address);
        p.setPrimaryAddress(address);
        xoManager2.currentTransaction().commit();
        xoManager2.currentTransaction().begin();
        Map<String, Object> params = new HashMap<>();
        params.put("name", "BAR");
        Person singleResult = xoManager2.createQuery("MATCH (p:Person{name:{name}}) RETURN p", Person.class).withParameters(params).execute().getSingleResult();

        PersonRepository personRepository = xoManager2.getRepository(PersonRepository.class);
        ResultIterable<Person> bar = personRepository.find("BAR");
        for (Person person : bar) {
            System.out.println(person);
        }
        p.getAddresses().remove(address);
        p.setPrimaryAddress(null);
        xoManager2.currentTransaction().commit();
        xoManager2.close();
        xoManager1.currentTransaction().begin();
        xoManager1.delete(person1);
        xoManager1.delete(person2);
        xoManager1.currentTransaction().commit();
        xoManager1.close();
    }

    @Test
    public void manyPersons() throws URISyntaxException {
        XOManagerFactory xmf = XO.createXOManagerFactory(getXoUnit());
        try (XOManager xm = xmf.createXOManager()) {
            for (int i = 0; i < 100; i++) {
                xm.currentTransaction().begin();
                for (int k = 0; k < 1000; k++) {
                    String name = "Foo_" + i + "_" + k;
                    Person person = xm.create((exampe) -> exampe.setName(name), Person.class);
                    for (int l = 0; l < 2; l++) {
                        String city = "City_" + i + "_" + k + "_" + l;
                        Address address = xm.create((example) -> example.setCity(city), Address.class);
                        person.getAddresses().add(address);
                    }
                }
                xm.currentTransaction().commit();
            }
        }
    }

    private XOUnit getXoUnit() throws URISyntaxException {
        Properties properties = new Properties();
        properties.setProperty("neo4j.remote.username", "neo4j");
        properties.setProperty("neo4j.remote.password", "admin");
        return XOUnit.builder().provider(Neo4jRemoteStoreProvider.class).uri(new URI("bolt://localhost:7687")).properties(properties).type(Person.class)
                .type(Customer.class).type(Address.class).type(PersonRepository.class).build();
    }
}
