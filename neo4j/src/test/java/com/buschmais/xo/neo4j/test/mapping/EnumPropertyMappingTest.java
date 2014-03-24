package com.buschmais.xo.neo4j.test.mapping;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractCdoManagerTest;
import com.buschmais.xo.neo4j.test.mapping.composite.A;
import com.buschmais.xo.neo4j.test.mapping.composite.Enumeration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.net.URISyntaxException;
import java.util.Collection;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class EnumPropertyMappingTest extends AbstractCdoManagerTest {

    public EnumPropertyMappingTest(XOUnit XOUnit) {
        super(XOUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getCdoUnits() throws URISyntaxException {
        return cdoUnits(A.class);
    }

    @Test
    public void enumerationLabel() {
        XOManager XOManager = getXOManager();
        XOManager.currentTransaction().begin();
        A a = XOManager.create(A.class);
        a.setEnumeration(Enumeration.FIRST);
        XOManager.currentTransaction().commit();
        XOManager.currentTransaction().begin();
        assertThat(a.getEnumeration(), equalTo(Enumeration.FIRST));
        assertThat(executeQuery("MATCH (a:A) WHERE a.enumeration='FIRST' RETURN a").getColumn("a"), hasItem(a));
        a.setEnumeration(Enumeration.SECOND);
        XOManager.currentTransaction().commit();
        XOManager.currentTransaction().begin();
        assertThat(a.getEnumeration(), equalTo(Enumeration.SECOND));
        assertThat(executeQuery("MATCH (a:A) WHERE a.enumeration='SECOND' RETURN a").getColumn("a"), hasItem(a));
        a.setEnumeration(null);
        XOManager.currentTransaction().commit();
        XOManager.currentTransaction().begin();
        assertThat(a.getEnumeration(), equalTo(null));
        XOManager.currentTransaction().commit();
    }

    @Test
    public void enumerationProperty() {
        XOManager XOManager = getXOManager();
        XOManager.currentTransaction().begin();
        A a = XOManager.create(A.class);
        a.setMappedEnumeration(Enumeration.FIRST);
        XOManager.currentTransaction().commit();
        XOManager.currentTransaction().begin();
        assertThat(a.getMappedEnumeration(), equalTo(Enumeration.FIRST));
        assertThat(executeQuery("MATCH (a:A) WHERE a.MAPPED_ENUMERATION='FIRST' RETURN a").getColumn("a"), hasItem(a));
        a.setMappedEnumeration(Enumeration.SECOND);
        XOManager.currentTransaction().commit();
        XOManager.currentTransaction().begin();
        assertThat(a.getMappedEnumeration(), equalTo(Enumeration.SECOND));
        assertThat(executeQuery("MATCH (a:A) WHERE a.MAPPED_ENUMERATION='SECOND' RETURN a").getColumn("a"), hasItem(a));
        a.setMappedEnumeration(null);
        XOManager.currentTransaction().commit();
        XOManager.currentTransaction().begin();
        assertThat(a.getMappedEnumeration(), equalTo(null));
        XOManager.currentTransaction().commit();
    }
}
