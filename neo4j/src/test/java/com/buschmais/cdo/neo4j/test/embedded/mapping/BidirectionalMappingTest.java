package com.buschmais.cdo.neo4j.test.embedded.mapping;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.neo4j.test.embedded.AbstractEmbeddedCdoManagerTest;
import com.buschmais.cdo.neo4j.test.embedded.mapping.composite.G;
import com.buschmais.cdo.neo4j.test.embedded.mapping.composite.H;
import org.hamcrest.Matchers;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.junit.Assert.assertThat;

public class BidirectionalMappingTest extends AbstractEmbeddedCdoManagerTest {

    @Override
    protected Class<?>[] getTypes() {
        return new Class<?>[]{G.class, H.class};
    }

    @Test
    public void oneToOne() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.begin();
        G g1 = cdoManager.create(G.class);
        H h = cdoManager.create(H.class);
        g1.setOneToOneH(h);
        cdoManager.commit();
        cdoManager.begin();
        assertThat(h.getOneToOneG(), equalTo(g1));
        cdoManager.commit();
        cdoManager.begin();
        G g2 = cdoManager.create(G.class);
        h.setOneToOneG(g2);
        cdoManager.commit();
        cdoManager.begin();
        assertThat(g1.getOneToOneH(), equalTo(null));
        assertThat(g2.getOneToOneH(), equalTo(h));
        cdoManager.commit();
    }

    @Test
    public void oneToMany() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.begin();
        G g1 = cdoManager.create(G.class);
        H h = cdoManager.create(H.class);
        g1.getOneToManyH().add(h);
        cdoManager.commit();
        cdoManager.begin();
        assertThat(h.getManyToOneG(), equalTo(g1));
        cdoManager.commit();
        cdoManager.begin();
        G g2 = cdoManager.create(G.class);
        h.setManyToOneG(g2);
        cdoManager.commit();
        cdoManager.begin();
        assertThat(g1.getOneToManyH().size(), equalTo(0));
        assertThat(g2.getOneToManyH(), hasItems(h));
        cdoManager.commit();
    }

    @Test
    public void ManyToMany() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.begin();
        G g1 = cdoManager.create(G.class);
        H h = cdoManager.create(H.class);
        g1.getManyToManyH().add(h);
        cdoManager.commit();
        cdoManager.begin();
        assertThat(h.getManyToManyG(), hasItems(g1));
        cdoManager.commit();
        cdoManager.begin();
        G g2 = cdoManager.create(G.class);
        assertThat(h.getManyToManyG().remove(g1), equalTo(true));
        h.getManyToManyG().add(g2);
        cdoManager.commit();
        cdoManager.begin();
        assertThat(g1.getManyToManyH().size(), equalTo(0));
        assertThat(g2.getManyToManyH(), hasItems(h));
        cdoManager.commit();
    }
}
