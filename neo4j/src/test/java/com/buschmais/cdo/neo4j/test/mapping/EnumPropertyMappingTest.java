package com.buschmais.cdo.neo4j.test.mapping;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.api.bootstrap.CdoUnit;
import com.buschmais.cdo.neo4j.test.AbstractCdoManagerTest;
import com.buschmais.cdo.neo4j.test.mapping.composite.A;
import com.buschmais.cdo.neo4j.test.mapping.composite.Enumeration;
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

    public EnumPropertyMappingTest(CdoUnit cdoUnit) {
        super(cdoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getCdoUnits() throws URISyntaxException {
        return cdoUnits(A.class);
    }

    @Test
    public void enumerationLabel() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        A a = cdoManager.create(A.class);
        a.setEnumeration(Enumeration.FIRST);
        cdoManager.currentTransaction().commit();
        cdoManager.currentTransaction().begin();
        assertThat(a.getEnumeration(), equalTo(Enumeration.FIRST));
        assertThat(executeQuery("MATCH (a:A) WHERE a.enumeration='FIRST' RETURN a").getColumn("a"), hasItem(a));
        a.setEnumeration(Enumeration.SECOND);
        cdoManager.currentTransaction().commit();
        cdoManager.currentTransaction().begin();
        assertThat(a.getEnumeration(), equalTo(Enumeration.SECOND));
        assertThat(executeQuery("MATCH (a:A) WHERE a.enumeration='SECOND' RETURN a").getColumn("a"), hasItem(a));
        a.setEnumeration(null);
        cdoManager.currentTransaction().commit();
        cdoManager.currentTransaction().begin();
        assertThat(a.getEnumeration(), equalTo(null));
        cdoManager.currentTransaction().commit();
    }

    @Test
    public void enumerationProperty() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        A a = cdoManager.create(A.class);
        a.setMappedEnumeration(Enumeration.FIRST);
        cdoManager.currentTransaction().commit();
        cdoManager.currentTransaction().begin();
        assertThat(a.getMappedEnumeration(), equalTo(Enumeration.FIRST));
        assertThat(executeQuery("MATCH (a:A) WHERE a.MAPPED_ENUMERATION='FIRST' RETURN a").getColumn("a"), hasItem(a));
        a.setMappedEnumeration(Enumeration.SECOND);
        cdoManager.currentTransaction().commit();
        cdoManager.currentTransaction().begin();
        assertThat(a.getMappedEnumeration(), equalTo(Enumeration.SECOND));
        assertThat(executeQuery("MATCH (a:A) WHERE a.MAPPED_ENUMERATION='SECOND' RETURN a").getColumn("a"), hasItem(a));
        a.setMappedEnumeration(null);
        cdoManager.currentTransaction().commit();
        cdoManager.currentTransaction().begin();
        assertThat(a.getMappedEnumeration(), equalTo(null));
        cdoManager.currentTransaction().commit();
    }
}
