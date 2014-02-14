package com.buschmais.cdo.neo4j.test.relation.qualified;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.api.bootstrap.CdoUnit;
import com.buschmais.cdo.neo4j.test.AbstractCdoManagerTest;
import com.buschmais.cdo.neo4j.test.relation.qualified.composite.A;
import com.buschmais.cdo.neo4j.test.relation.qualified.composite.B;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.net.URISyntaxException;
import java.util.Collection;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class QualifiedRelationTest extends AbstractCdoManagerTest {

    public QualifiedRelationTest(CdoUnit cdoUnit) {
        super(cdoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getCdoUnits() throws URISyntaxException {
        return cdoUnits(asList(Database.MEMORY), asList(A.class, B.class));
    }

    @Test
    public void oneToOne() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        A a = cdoManager.create(A.class);
        B b1 = cdoManager.create(B.class);
        a.setOneToOne(b1);
        cdoManager.currentTransaction().commit();
        cdoManager.currentTransaction().begin();
        assertThat(a.getOneToOne(), equalTo(b1));
        assertThat(b1.getOneToOne(), equalTo(a));
        assertThat(executeQuery("MATCH (a:A)-[:OneToOne]->(b:B) RETURN b").getColumn("b"), hasItem(b1));
        B b2 = cdoManager.create(B.class);
        a.setOneToOne(b2);
        cdoManager.currentTransaction().commit();
        cdoManager.currentTransaction().begin();
        assertThat(a.getOneToOne(), equalTo(b2));
        assertThat(b2.getOneToOne(), equalTo(a));
        assertThat(b1.getOneToOne(), equalTo(null));
        assertThat(executeQuery("MATCH (a:A)-[:OneToOne]->(b:B) RETURN b").getColumn("b"), hasItem(b2));
        a.setOneToOne(null);
        cdoManager.currentTransaction().commit();
        cdoManager.currentTransaction().begin();
        assertThat(a.getOneToOne(), equalTo(null));
        assertThat(b1.getOneToOne(), equalTo(null));
        assertThat(b2.getOneToOne(), equalTo(null));
        cdoManager.currentTransaction().commit();
    }

    @Test
    public void oneToMany() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        A a = cdoManager.create(A.class);
        B b1 = cdoManager.create(B.class);
        B b2 = cdoManager.create(B.class);
        a.getOneToMany().add(b1);
        a.getOneToMany().add(b2);
        cdoManager.currentTransaction().commit();
        cdoManager.currentTransaction().begin();
        assertThat(a.getOneToMany(), hasItems(b1, b2));
        assertThat(b1.getManyToOne(), equalTo(a));
        assertThat(b2.getManyToOne(), equalTo(a));
        assertThat(executeQuery("MATCH (a:A)-[:OneToMany]->(b:B) RETURN b").<B>getColumn("b"), hasItems(b1, b2));
        a.getOneToMany().remove(b1);
        a.getOneToMany().remove(b2);
        B b3 = cdoManager.create(B.class);
        B b4 = cdoManager.create(B.class);
        a.getOneToMany().add(b3);
        a.getOneToMany().add(b4);
        cdoManager.currentTransaction().commit();
        cdoManager.currentTransaction().begin();
        assertThat(a.getOneToMany(), hasItems(b3, b4));
        assertThat(b1.getManyToOne(), equalTo(null));
        assertThat(b2.getManyToOne(), equalTo(null));
        assertThat(b3.getManyToOne(), equalTo(a));
        assertThat(b4.getManyToOne(), equalTo(a));
        assertThat(executeQuery("MATCH (a:A)-[:OneToMany]->(b:B) RETURN b").<B>getColumn("b"), hasItems(b3, b4));
        cdoManager.currentTransaction().commit();
    }

    @Test
    public void manyToMany() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        A a1 = cdoManager.create(A.class);
        A a2 = cdoManager.create(A.class);
        B b1 = cdoManager.create(B.class);
        B b2 = cdoManager.create(B.class);
        a1.getManyToMany().add(b1);
        a1.getManyToMany().add(b2);
        a2.getManyToMany().add(b1);
        a2.getManyToMany().add(b2);
        cdoManager.currentTransaction().commit();
        cdoManager.currentTransaction().begin();
        assertThat(a1.getManyToMany(), hasItems(b1, b2));
        assertThat(a2.getManyToMany(), hasItems(b1, b2));
        assertThat(b1.getManyToMany(), hasItems(a1, a2));
        assertThat(b2.getManyToMany(), hasItems(a1, a2));
        assertThat(executeQuery("MATCH (a:A)-[:ManyToMany]->(b:B) RETURN a, collect(b) as listOfB ORDER BY ID(a)").<A>getColumn("a"), hasItems(a1, a2));
        assertThat(executeQuery("MATCH (a:A)-[:ManyToMany]->(b:B) RETURN a, collect(b) as listOfB ORDER BY ID(a)").<Iterable<B>>getColumn("listOfB"), hasItems(hasItems(b1, b2), hasItems(b1, b2)));
        a1.getManyToMany().remove(b1);
        a2.getManyToMany().remove(b1);
        cdoManager.currentTransaction().commit();
        cdoManager.currentTransaction().begin();
        assertThat(a1.getManyToMany(), hasItems(b2));
        assertThat(a2.getManyToMany(), hasItems(b2));
        assertThat(b1.getManyToMany().isEmpty(), equalTo(true));
        assertThat(b2.getManyToMany(), hasItems(a1, a2));
        cdoManager.currentTransaction().commit();
    }
}
