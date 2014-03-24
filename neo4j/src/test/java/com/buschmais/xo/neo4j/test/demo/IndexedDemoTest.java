package com.buschmais.xo.neo4j.test.demo;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractCdoManagerTest;
import com.buschmais.xo.neo4j.test.demo.composite.Group;
import com.buschmais.xo.neo4j.test.demo.composite.Person;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.net.URISyntaxException;
import java.util.Collection;

@RunWith(Parameterized.class)
public class IndexedDemoTest extends AbstractCdoManagerTest {

    public IndexedDemoTest(XOUnit XOUnit) {
        super(XOUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getCdoUnits() throws URISyntaxException {
        return cdoUnits(Group.class, Person.class);
    }

    @Test
    public void test() {
        XOManager XOManager = getXOManager();
        XOManager.currentTransaction().begin();
        Person person1 = XOManager.create(Person.class);
        person1.setName("Peter");
        XOManager.currentTransaction().commit();
        XOManager.currentTransaction().begin();
        Person person2 = XOManager.find(Person.class, "Peter").getSingleResult();
        Assert.assertThat(person2, Matchers.equalTo(person1));
        XOManager.currentTransaction().commit();
    }
}
