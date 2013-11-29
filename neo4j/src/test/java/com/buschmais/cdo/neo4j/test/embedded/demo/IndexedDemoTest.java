package com.buschmais.cdo.neo4j.test.embedded.demo;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.neo4j.test.embedded.AbstractEmbeddedCdoManagerTest;
import com.buschmais.cdo.neo4j.test.embedded.demo.composite.Group;
import com.buschmais.cdo.neo4j.test.embedded.demo.composite.Person;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

public class IndexedDemoTest extends AbstractEmbeddedCdoManagerTest {
    @Override
    protected Class<?>[] getTypes() {
        return new Class<?>[]{Group.class, Person.class};
    }

    @Test
    public void test() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.begin();
        Person person1 = cdoManager.create(Person.class);
        person1.setName("Peter");
        cdoManager.commit();
        cdoManager.begin();
        Person person2 = cdoManager.find(Person.class, "Peter").getSingleResult();
        Assert.assertThat(person2, Matchers.equalTo(person1));
        cdoManager.commit();
    }
}
