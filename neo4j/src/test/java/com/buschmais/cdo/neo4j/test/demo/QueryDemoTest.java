package com.buschmais.cdo.neo4j.test.demo;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.api.bootstrap.CdoUnit;
import com.buschmais.cdo.neo4j.test.AbstractCdoManagerTest;
import com.buschmais.cdo.neo4j.test.demo.composite.Group;
import com.buschmais.cdo.neo4j.test.demo.composite.Person;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.net.URISyntaxException;
import java.util.Collection;

import static com.buschmais.cdo.neo4j.test.demo.composite.Group.MemberByName;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class QueryDemoTest extends AbstractCdoManagerTest {

    public QueryDemoTest(CdoUnit cdoUnit) {
        super(cdoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getCdoUnits() throws URISyntaxException {
        return cdoUnits(asList(Database.MEMORY), asList(Group.class, Person.class));
    }

    @Test
    public void test() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        Group group = cdoManager.create(Group.class);
        Person person1 = cdoManager.create(Person.class);
        person1.setName("Peter");
        group.getMembers().add(person1);
        Person person2 = cdoManager.create(Person.class);
        person2.setName("Dirk");
        group.getMembers().add(person2);
        cdoManager.currentTransaction().commit();
        cdoManager.currentTransaction().begin();
        MemberByName memberByName = cdoManager.createQuery(MemberByName.class).withParameter("this", group).withParameter("name", "Peter").execute().getSingleResult();
        Person peter = memberByName.getMember();
        assertThat(peter, equalTo(person1));
        cdoManager.currentTransaction().commit();
        cdoManager.currentTransaction().begin();
        memberByName = group.getMemberByName("Peter");
        peter = memberByName.getMember();
        assertThat(peter, equalTo(person1));
        cdoManager.currentTransaction().commit();
    }
}
