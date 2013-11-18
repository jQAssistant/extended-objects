package com.buschmais.cdo.neo4j.test.demo;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.api.Query;
import com.buschmais.cdo.neo4j.test.AbstractCdoManagerTest;
import com.buschmais.cdo.neo4j.test.demo.composite.Group;
import com.buschmais.cdo.neo4j.test.demo.composite.Person;
import org.junit.Test;

import static com.buschmais.cdo.api.Query.Result.CompositeRowObject;

public class QueryDemoTest  extends AbstractCdoManagerTest{
    @Override
    protected Class<?>[] getTypes() {
        return new Class<?>[] {Group.class, Person.class};
    }

    @Test
    public void test() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.begin();
        Group group = cdoManager.create(Group.class);
        Person person1 = cdoManager.create(Person.class);
        person1.setName("Peter");
        group.getMembers().add(person1);
        Person person2 = cdoManager.create(Person.class);
        person2.setName("Dirk");
        group.getMembers().add(person2);
        cdoManager.commit();
        cdoManager.begin();
        Group.PersonByName peters = group.getPersonByName("Peter");
        System.err.println(peters.getPerson().getName());
        cdoManager.commit();
    }
}
