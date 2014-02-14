package com.buschmais.cdo.neo4j.test.demo;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.api.bootstrap.CdoUnit;
import com.buschmais.cdo.neo4j.test.AbstractCdoManagerTest;
import com.buschmais.cdo.neo4j.test.demo.composite.Group;
import com.buschmais.cdo.neo4j.test.demo.composite.Person;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.net.URISyntaxException;
import java.util.Collection;

@RunWith(Parameterized.class)
public class IndexedDemoTest extends AbstractCdoManagerTest {

    public IndexedDemoTest(CdoUnit cdoUnit) {
        super(cdoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getCdoUnits() throws URISyntaxException {
        return cdoUnits(Group.class, Person.class);
    }

    @Test
    public void test() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        Person person1 = cdoManager.create(Person.class);
        person1.setName("Peter");
        cdoManager.currentTransaction().commit();
        cdoManager.currentTransaction().begin();
        Person person2 = cdoManager.find(Person.class, "Peter").getSingleResult();
        Assert.assertThat(person2, Matchers.equalTo(person1));
        cdoManager.currentTransaction().commit();
    }
}
