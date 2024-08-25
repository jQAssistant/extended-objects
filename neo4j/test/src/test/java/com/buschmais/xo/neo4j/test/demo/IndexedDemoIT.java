package com.buschmais.xo.neo4j.test.demo;

import java.util.Collection;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractNeo4JXOManagerIT;
import com.buschmais.xo.neo4j.test.demo.composite.Group;
import com.buschmais.xo.neo4j.test.demo.composite.Person;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class IndexedDemoIT extends AbstractNeo4JXOManagerIT {

    public IndexedDemoIT(XOUnit xoUnit) {
        super(xoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getXOUnits() {
        return xoUnits(Group.class, Person.class);
    }

    @Test
    public void test() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction()
            .begin();
        Person person1 = xoManager.create(Person.class);
        person1.setName("Peter");
        xoManager.currentTransaction()
            .commit();
        xoManager.currentTransaction()
            .begin();
        Person person2 = xoManager.find(Person.class, "Peter")
            .getSingleResult();
        assertThat(person2).isEqualTo(person1);
        xoManager.currentTransaction()
            .commit();
    }
}
