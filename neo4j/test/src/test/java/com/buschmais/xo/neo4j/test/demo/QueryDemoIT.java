package com.buschmais.xo.neo4j.test.demo;

import static com.buschmais.xo.neo4j.test.demo.composite.Group.MemberByName;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractNeo4JXOManagerIT;
import com.buschmais.xo.neo4j.test.demo.composite.Group;
import com.buschmais.xo.neo4j.test.demo.composite.Person;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class QueryDemoIT extends AbstractNeo4JXOManagerIT {

    public QueryDemoIT(XOUnit xoUnit) {
        super(xoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getXOUnits() {
        return xoUnits(Group.class, Person.class);
    }

    @Test
    public void test() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction().begin();
        Group group = xoManager.create(Group.class);
        Person person1 = xoManager.create(Person.class);
        person1.setName("Peter");
        group.getMembers().add(person1);
        Person person2 = xoManager.create(Person.class);
        person2.setName("Dirk");
        group.getMembers().add(person2);
        xoManager.currentTransaction().commit();
        xoManager.currentTransaction().begin();
        MemberByName memberByName = xoManager.createQuery(MemberByName.class).withParameter("this", group).withParameter("name", "Peter").execute()
                .getSingleResult();
        Person peter = memberByName.getMember();
        assertThat(peter).isEqualTo(person1);
        xoManager.currentTransaction().commit();
        xoManager.currentTransaction().begin();
        memberByName = group.getMemberByName("Peter");
        peter = memberByName.getMember();
        assertThat(peter).isEqualTo(person1);
        xoManager.currentTransaction().commit();
    }
}
