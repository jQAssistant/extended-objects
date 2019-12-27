package com.buschmais.xo.neo4j.test.mapping;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertThat;

import java.util.Collection;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractNeo4jXOManagerTest;
import com.buschmais.xo.neo4j.test.mapping.composite.A;
import com.buschmais.xo.neo4j.test.mapping.composite.Enumeration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class EnumPropertyMappingTest extends AbstractNeo4jXOManagerTest {

    public EnumPropertyMappingTest(XOUnit xoUnit) {
        super(xoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getXOUnits() {
        return xoUnits(A.class);
    }

    @Test
    public void enumerationLabel() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction().begin();
        A a = xoManager.create(A.class);
        a.setEnumeration(Enumeration.FIRST);
        xoManager.currentTransaction().commit();
        xoManager.currentTransaction().begin();
        assertThat(a.getEnumeration(), equalTo(Enumeration.FIRST));
        assertThat(executeQuery("MATCH (a:A) WHERE a.enumeration='FIRST' RETURN a").getColumn("a"), hasItem(a));
        a.setEnumeration(Enumeration.SECOND);
        xoManager.currentTransaction().commit();
        xoManager.currentTransaction().begin();
        assertThat(a.getEnumeration(), equalTo(Enumeration.SECOND));
        assertThat(executeQuery("MATCH (a:A) WHERE a.enumeration='SECOND' RETURN a").getColumn("a"), hasItem(a));
        a.setEnumeration(null);
        xoManager.currentTransaction().commit();
        xoManager.currentTransaction().begin();
        assertThat(a.getEnumeration(), equalTo(null));
        xoManager.currentTransaction().commit();
    }

    @Test
    public void enumerationProperty() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction().begin();
        A a = xoManager.create(A.class);
        a.setMappedEnumeration(Enumeration.FIRST);
        xoManager.currentTransaction().commit();
        xoManager.currentTransaction().begin();
        assertThat(a.getMappedEnumeration(), equalTo(Enumeration.FIRST));
        assertThat(executeQuery("MATCH (a:A) WHERE a.MAPPED_ENUMERATION='FIRST' RETURN a").getColumn("a"), hasItem(a));
        a.setMappedEnumeration(Enumeration.SECOND);
        xoManager.currentTransaction().commit();
        xoManager.currentTransaction().begin();
        assertThat(a.getMappedEnumeration(), equalTo(Enumeration.SECOND));
        assertThat(executeQuery("MATCH (a:A) WHERE a.MAPPED_ENUMERATION='SECOND' RETURN a").getColumn("a"), hasItem(a));
        a.setMappedEnumeration(null);
        xoManager.currentTransaction().commit();
        xoManager.currentTransaction().begin();
        assertThat(a.getMappedEnumeration(), equalTo(null));
        xoManager.currentTransaction().commit();
    }
}
