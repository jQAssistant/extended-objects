package com.buschmais.cdo.neo4j.test.mapping;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.api.bootstrap.CdoUnit;
import com.buschmais.cdo.neo4j.test.AbstractCdoManagerTest;
import com.buschmais.cdo.neo4j.test.mapping.composite.A;
import com.buschmais.cdo.neo4j.test.mapping.composite.B;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class CollectionPropertyMappingTest extends AbstractCdoManagerTest {

    public CollectionPropertyMappingTest(CdoUnit cdoUnit) {
        super(cdoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getCdoUnits() throws URISyntaxException {
        return cdoUnits(A.class, B.class);
    }

    @Test
    public void setProperty() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        A a = cdoManager.create(A.class);
        B b = cdoManager.create(B.class);
        Set<B> setOfB = a.getSetOfB();
        assertThat(setOfB.add(b), equalTo(true));
        assertThat(setOfB.add(b), equalTo(false));
        cdoManager.currentTransaction().commit();
        cdoManager.currentTransaction().begin();
        assertThat(setOfB.size(), equalTo(1));
        assertThat(setOfB.remove(b), equalTo(true));
        assertThat(setOfB.remove(b), equalTo(false));
        cdoManager.currentTransaction().commit();
    }

    @Test
    public void mappedSetProperty() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        A a = cdoManager.create(A.class);
        B b = cdoManager.create(B.class);
        a.getMappedSetOfB().add(b);
        cdoManager.currentTransaction().commit();
        cdoManager.currentTransaction().begin();
        TestResult result = executeQuery("match (a:A)-[:MAPPED_SET_OF_B]->(b) return b");
        assertThat(result.getColumn("b").size(), equalTo(1));
        assertThat(result.getColumn("b"), hasItem(b));
        cdoManager.currentTransaction().commit();
    }

    @Test
    public void listProperty() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        A a = cdoManager.create(A.class);
        B b = cdoManager.create(B.class);
        List<B> listOfB = a.getListOfB();
        assertThat(listOfB.add(b), equalTo(true));
        assertThat(listOfB.add(b), equalTo(true));
        cdoManager.currentTransaction().commit();
        cdoManager.currentTransaction().begin();
        assertThat(listOfB.size(), equalTo(2));
        assertThat(listOfB.remove(b), equalTo(true));
        assertThat(listOfB.remove(b), equalTo(true));
        assertThat(listOfB.remove(b), equalTo(false));
        cdoManager.currentTransaction().commit();
    }

    @Test
    public void mappedListProperty() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        A a = cdoManager.create(A.class);
        B b = cdoManager.create(B.class);
        a.getMappedListOfB().add(b);
        a.getMappedListOfB().add(b);
        cdoManager.currentTransaction().commit();
        cdoManager.currentTransaction().begin();
        TestResult result = executeQuery("match (a:A)-[:MAPPED_LIST_OF_B]->(b) return b");
        assertThat(result.getColumn("b").size(), equalTo(2));
        assertThat(result.getColumn("b"), hasItem(b));
        cdoManager.currentTransaction().commit();
    }
}
