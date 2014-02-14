package com.buschmais.cdo.neo4j.test.mapping;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.api.bootstrap.CdoUnit;
import com.buschmais.cdo.neo4j.test.AbstractCdoManagerTest;
import com.buschmais.cdo.neo4j.test.mapping.composite.G;
import com.buschmais.cdo.neo4j.test.mapping.composite.H;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.net.URISyntaxException;
import java.util.Collection;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class BidirectionalMappingTest extends AbstractCdoManagerTest {

    public BidirectionalMappingTest(CdoUnit cdoUnit) {
        super(cdoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getCdoUnits() throws URISyntaxException {
        return cdoUnits(G.class, H.class);
    }

    @Test
    public void oneToOne() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        G g1 = cdoManager.create(G.class);
        H h = cdoManager.create(H.class);
        g1.setOneToOneH(h);
        cdoManager.currentTransaction().commit();
        cdoManager.currentTransaction().begin();
        assertThat(h.getOneToOneG(), equalTo(g1));
        cdoManager.currentTransaction().commit();
        cdoManager.currentTransaction().begin();
        G g2 = cdoManager.create(G.class);
        h.setOneToOneG(g2);
        cdoManager.currentTransaction().commit();
        cdoManager.currentTransaction().begin();
        assertThat(g1.getOneToOneH(), equalTo(null));
        assertThat(g2.getOneToOneH(), equalTo(h));
        cdoManager.currentTransaction().commit();
    }

    @Test
    public void oneToMany() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        G g1 = cdoManager.create(G.class);
        H h = cdoManager.create(H.class);
        g1.getOneToManyH().add(h);
        cdoManager.currentTransaction().commit();
        cdoManager.currentTransaction().begin();
        assertThat(h.getManyToOneG(), equalTo(g1));
        cdoManager.currentTransaction().commit();
        cdoManager.currentTransaction().begin();
        G g2 = cdoManager.create(G.class);
        h.setManyToOneG(g2);
        cdoManager.currentTransaction().commit();
        cdoManager.currentTransaction().begin();
        assertThat(g1.getOneToManyH().size(), equalTo(0));
        assertThat(g2.getOneToManyH(), hasItems(h));
        cdoManager.currentTransaction().commit();
    }

    @Test
    public void ManyToMany() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        G g1 = cdoManager.create(G.class);
        H h = cdoManager.create(H.class);
        g1.getManyToManyH().add(h);
        cdoManager.currentTransaction().commit();
        cdoManager.currentTransaction().begin();
        assertThat(h.getManyToManyG(), hasItems(g1));
        cdoManager.currentTransaction().commit();
        cdoManager.currentTransaction().begin();
        G g2 = cdoManager.create(G.class);
        assertThat(h.getManyToManyG().remove(g1), equalTo(true));
        h.getManyToManyG().add(g2);
        cdoManager.currentTransaction().commit();
        cdoManager.currentTransaction().begin();
        assertThat(g1.getManyToManyH().size(), equalTo(0));
        assertThat(g2.getManyToManyH(), hasItems(h));
        cdoManager.currentTransaction().commit();
    }
}
