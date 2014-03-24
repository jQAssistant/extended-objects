package com.buschmais.xo.neo4j.test.label;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractXOManagerTest;
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
public class LabelTest extends AbstractXOManagerTest {

    public LabelTest(XOUnit xoUnit) {
        super(xoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getXOUnits() throws URISyntaxException {
        return xoUnits(ImplicitLabel.class, ExplicitLabel.class);
    }

    @Test
    public void implicitLabel() {
        XOManager xoManager = getXoManager();
        xoManager.currentTransaction().begin();
        ImplicitLabel implicitLabel = xoManager.create(ImplicitLabel.class);
        assertThat(executeQuery("MATCH (n:ImplicitLabel) RETURN n").getColumn("n"), hasItem(implicitLabel));
        xoManager.currentTransaction().commit();
    }


    @Test
    public void explicitLabel() {
        XOManager xoManager = getXoManager();
        xoManager.currentTransaction().begin();
        ExplicitLabel explicitLabel = xoManager.create(ExplicitLabel.class);
        assertThat(executeQuery("MATCH (n:EXPLICIT_LABEL) RETURN n").getColumn("n"), hasItem(explicitLabel));
        xoManager.currentTransaction().commit();
    }
}
