package com.buschmais.xo.neo4j.test.mapping;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractCdoManagerTest;
import com.buschmais.xo.neo4j.test.mapping.composite.A;
import com.buschmais.xo.neo4j.test.mapping.composite.B;
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

    public CollectionPropertyMappingTest(XOUnit XOUnit) {
        super(XOUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getCdoUnits() throws URISyntaxException {
        return cdoUnits(A.class, B.class);
    }

    @Test
    public void setProperty() {
        XOManager XOManager = getXOManager();
        XOManager.currentTransaction().begin();
        A a = XOManager.create(A.class);
        B b = XOManager.create(B.class);
        Set<B> setOfB = a.getSetOfB();
        assertThat(setOfB.add(b), equalTo(true));
        assertThat(setOfB.add(b), equalTo(false));
        XOManager.currentTransaction().commit();
        XOManager.currentTransaction().begin();
        assertThat(setOfB.size(), equalTo(1));
        assertThat(setOfB.remove(b), equalTo(true));
        assertThat(setOfB.remove(b), equalTo(false));
        XOManager.currentTransaction().commit();
    }

    @Test
    public void mappedSetProperty() {
        XOManager XOManager = getXOManager();
        XOManager.currentTransaction().begin();
        A a = XOManager.create(A.class);
        B b = XOManager.create(B.class);
        a.getMappedSetOfB().add(b);
        XOManager.currentTransaction().commit();
        XOManager.currentTransaction().begin();
        TestResult result = executeQuery("match (a:A)-[:MAPPED_SET_OF_B]->(b) return b");
        assertThat(result.getColumn("b").size(), equalTo(1));
        assertThat(result.getColumn("b"), hasItem(b));
        XOManager.currentTransaction().commit();
    }

    @Test
    public void listProperty() {
        XOManager XOManager = getXOManager();
        XOManager.currentTransaction().begin();
        A a = XOManager.create(A.class);
        B b = XOManager.create(B.class);
        List<B> listOfB = a.getListOfB();
        assertThat(listOfB.add(b), equalTo(true));
        assertThat(listOfB.add(b), equalTo(true));
        XOManager.currentTransaction().commit();
        XOManager.currentTransaction().begin();
        assertThat(listOfB.size(), equalTo(2));
        assertThat(listOfB.remove(b), equalTo(true));
        assertThat(listOfB.remove(b), equalTo(true));
        assertThat(listOfB.remove(b), equalTo(false));
        XOManager.currentTransaction().commit();
    }

    @Test
    public void mappedListProperty() {
        XOManager XOManager = getXOManager();
        XOManager.currentTransaction().begin();
        A a = XOManager.create(A.class);
        B b = XOManager.create(B.class);
        a.getMappedListOfB().add(b);
        a.getMappedListOfB().add(b);
        XOManager.currentTransaction().commit();
        XOManager.currentTransaction().begin();
        TestResult result = executeQuery("match (a:A)-[:MAPPED_LIST_OF_B]->(b) return b");
        assertThat(result.getColumn("b").size(), equalTo(2));
        assertThat(result.getColumn("b"), hasItem(b));
        XOManager.currentTransaction().commit();
    }
}
