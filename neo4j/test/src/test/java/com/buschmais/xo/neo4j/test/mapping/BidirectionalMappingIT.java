package com.buschmais.xo.neo4j.test.mapping;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.IsCollectionContaining.hasItems;

import java.util.Collection;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractNeo4JXOManagerIT;
import com.buschmais.xo.neo4j.test.mapping.composite.G;
import com.buschmais.xo.neo4j.test.mapping.composite.H;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class BidirectionalMappingIT extends AbstractNeo4JXOManagerIT {

    public BidirectionalMappingIT(XOUnit xoUnit) {
        super(xoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getXOUnits() {
        return xoUnits(G.class, H.class);
    }

    @Test
    public void oneToOne() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction().begin();
        G g1 = xoManager.create(G.class);
        H h = xoManager.create(H.class);
        g1.setOneToOneH(h);
        xoManager.currentTransaction().commit();
        xoManager.currentTransaction().begin();
        assertThat(h.getOneToOneG(), equalTo(g1));
        xoManager.currentTransaction().commit();
        xoManager.currentTransaction().begin();
        G g2 = xoManager.create(G.class);
        h.setOneToOneG(g2);
        xoManager.currentTransaction().commit();
        xoManager.currentTransaction().begin();
        assertThat(g1.getOneToOneH(), equalTo(null));
        assertThat(g2.getOneToOneH(), equalTo(h));
        xoManager.currentTransaction().commit();
    }

    @Test
    public void oneToMany() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction().begin();
        G g1 = xoManager.create(G.class);
        H h = xoManager.create(H.class);
        g1.getOneToManyH().add(h);
        xoManager.currentTransaction().commit();
        xoManager.currentTransaction().begin();
        assertThat(h.getManyToOneG(), equalTo(g1));
        xoManager.currentTransaction().commit();
        xoManager.currentTransaction().begin();
        G g2 = xoManager.create(G.class);
        h.setManyToOneG(g2);
        xoManager.currentTransaction().commit();
        xoManager.currentTransaction().begin();
        assertThat(g1.getOneToManyH().size(), equalTo(0));
        assertThat(g2.getOneToManyH(), hasItems(h));
        xoManager.currentTransaction().commit();
    }

    @Test
    public void ManyToMany() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction().begin();
        G g1 = xoManager.create(G.class);
        H h = xoManager.create(H.class);
        g1.getManyToManyH().add(h);
        xoManager.currentTransaction().commit();
        xoManager.currentTransaction().begin();
        assertThat(h.getManyToManyG(), hasItems(g1));
        xoManager.currentTransaction().commit();
        xoManager.currentTransaction().begin();
        G g2 = xoManager.create(G.class);
        assertThat(h.getManyToManyG().remove(g1), equalTo(true));
        h.getManyToManyG().add(g2);
        xoManager.currentTransaction().commit();
        xoManager.currentTransaction().begin();
        assertThat(g1.getManyToManyH().size(), equalTo(0));
        assertThat(g2.getManyToManyH(), hasItems(h));
        xoManager.currentTransaction().commit();
    }
}
