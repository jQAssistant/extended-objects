package com.buschmais.xo.neo4j.test.demo;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractCdoManagerTest;
import com.buschmais.xo.neo4j.test.demo.composite.Group;
import com.buschmais.xo.neo4j.test.demo.composite.Person;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.net.URISyntaxException;
import java.util.Collection;

import static com.buschmais.xo.neo4j.test.demo.composite.Group.MemberByName;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class QueryDemoTest extends AbstractCdoManagerTest {

    public QueryDemoTest(XOUnit XOUnit) {
        super(XOUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getCdoUnits() throws URISyntaxException {
        return cdoUnits(asList(Database.MEMORY), asList(Group.class, Person.class));
    }

    @Test
    public void test() {
        XOManager XOManager = getXOManager();
        XOManager.currentTransaction().begin();
        Group group = XOManager.create(Group.class);
        Person person1 = XOManager.create(Person.class);
        person1.setName("Peter");
        group.getMembers().add(person1);
        Person person2 = XOManager.create(Person.class);
        person2.setName("Dirk");
        group.getMembers().add(person2);
        XOManager.currentTransaction().commit();
        XOManager.currentTransaction().begin();
        MemberByName memberByName = XOManager.createQuery(MemberByName.class).withParameter("this", group).withParameter("name", "Peter").execute().getSingleResult();
        Person peter = memberByName.getMember();
        assertThat(peter, equalTo(person1));
        XOManager.currentTransaction().commit();
        XOManager.currentTransaction().begin();
        memberByName = group.getMemberByName("Peter");
        peter = memberByName.getMember();
        assertThat(peter, equalTo(person1));
        XOManager.currentTransaction().commit();
    }
}
