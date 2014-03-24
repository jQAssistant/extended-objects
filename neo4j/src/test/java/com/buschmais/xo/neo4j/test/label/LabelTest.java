package com.buschmais.xo.neo4j.test.label;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractCdoManagerTest;
import com.buschmais.xo.neo4j.test.label.composite.ExplicitLabel;
import com.buschmais.xo.neo4j.test.label.composite.ImplicitLabel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.net.URISyntaxException;
import java.util.Collection;

import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class LabelTest extends AbstractCdoManagerTest {

    public LabelTest(XOUnit XOUnit) {
        super(XOUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getCdoUnits() throws URISyntaxException {
        return cdoUnits(ImplicitLabel.class, ExplicitLabel.class);
    }

    @Test
    public void implicitLabel() {
        XOManager XOManager = getXOManager();
        XOManager.currentTransaction().begin();
        ImplicitLabel implicitLabel = XOManager.create(ImplicitLabel.class);
        assertThat(executeQuery("MATCH (n:ImplicitLabel) RETURN n").getColumn("n"), hasItem(implicitLabel));
        XOManager.currentTransaction().commit();
    }


    @Test
    public void explicitLabel() {
        XOManager XOManager = getXOManager();
        XOManager.currentTransaction().begin();
        ExplicitLabel explicitLabel = XOManager.create(ExplicitLabel.class);
        assertThat(executeQuery("MATCH (n:EXPLICIT_LABEL) RETURN n").getColumn("n"), hasItem(explicitLabel));
        XOManager.currentTransaction().commit();
    }
}
