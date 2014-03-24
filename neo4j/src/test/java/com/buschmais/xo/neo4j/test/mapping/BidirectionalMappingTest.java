package com.buschmais.xo.neo4j.test.mapping;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractCdoManagerTest;
import com.buschmais.xo.neo4j.test.mapping.composite.G;
import com.buschmais.xo.neo4j.test.mapping.composite.H;
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

    public BidirectionalMappingTest(XOUnit XOUnit) {
        super(XOUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getCdoUnits() throws URISyntaxException {
        return cdoUnits(G.class, H.class);
    }

    @Test
    public void oneToOne() {
        XOManager XOManager = getXOManager();
        XOManager.currentTransaction().begin();
        G g1 = XOManager.create(G.class);
        H h = XOManager.create(H.class);
        g1.setOneToOneH(h);
        XOManager.currentTransaction().commit();
        XOManager.currentTransaction().begin();
        assertThat(h.getOneToOneG(), equalTo(g1));
        XOManager.currentTransaction().commit();
        XOManager.currentTransaction().begin();
        G g2 = XOManager.create(G.class);
        h.setOneToOneG(g2);
        XOManager.currentTransaction().commit();
        XOManager.currentTransaction().begin();
        assertThat(g1.getOneToOneH(), equalTo(null));
        assertThat(g2.getOneToOneH(), equalTo(h));
        XOManager.currentTransaction().commit();
    }

    @Test
    public void oneToMany() {
        XOManager XOManager = getXOManager();
        XOManager.currentTransaction().begin();
        G g1 = XOManager.create(G.class);
        H h = XOManager.create(H.class);
        g1.getOneToManyH().add(h);
        XOManager.currentTransaction().commit();
        XOManager.currentTransaction().begin();
        assertThat(h.getManyToOneG(), equalTo(g1));
        XOManager.currentTransaction().commit();
        XOManager.currentTransaction().begin();
        G g2 = XOManager.create(G.class);
        h.setManyToOneG(g2);
        XOManager.currentTransaction().commit();
        XOManager.currentTransaction().begin();
        assertThat(g1.getOneToManyH().size(), equalTo(0));
        assertThat(g2.getOneToManyH(), hasItems(h));
        XOManager.currentTransaction().commit();
    }

    @Test
    public void ManyToMany() {
        XOManager XOManager = getXOManager();
        XOManager.currentTransaction().begin();
        G g1 = XOManager.create(G.class);
        H h = XOManager.create(H.class);
        g1.getManyToManyH().add(h);
        XOManager.currentTransaction().commit();
        XOManager.currentTransaction().begin();
        assertThat(h.getManyToManyG(), hasItems(g1));
        XOManager.currentTransaction().commit();
        XOManager.currentTransaction().begin();
        G g2 = XOManager.create(G.class);
        assertThat(h.getManyToManyG().remove(g1), equalTo(true));
        h.getManyToManyG().add(g2);
        XOManager.currentTransaction().commit();
        XOManager.currentTransaction().begin();
        assertThat(g1.getManyToManyH().size(), equalTo(0));
        assertThat(g2.getManyToManyH(), hasItems(h));
        XOManager.currentTransaction().commit();
    }
}
