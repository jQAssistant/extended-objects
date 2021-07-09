package com.buschmais.xo.neo4j.test.label;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsCollectionContaining.hasItem;

import java.util.Collection;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractNeo4JXOManagerIT;
import com.buschmais.xo.neo4j.test.label.composite.ExplicitLabel;
import com.buschmais.xo.neo4j.test.label.composite.ImplicitLabel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class LabelIT extends AbstractNeo4JXOManagerIT {

    public LabelIT(XOUnit xoUnit) {
        super(xoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getXOUnits() {
        return xoUnits(ImplicitLabel.class, ExplicitLabel.class);
    }

    @Test
    public void implicitLabel() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction().begin();
        ImplicitLabel implicitLabel = xoManager.create(ImplicitLabel.class);
        assertThat(executeQuery("MATCH (n:ImplicitLabel) RETURN n").getColumn("n"), hasItem(implicitLabel));
        xoManager.currentTransaction().commit();
    }

    @Test
    public void explicitLabel() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction().begin();
        ExplicitLabel explicitLabel = xoManager.create(ExplicitLabel.class);
        assertThat(executeQuery("MATCH (n:EXPLICIT_LABEL) RETURN n").getColumn("n"), hasItem(explicitLabel));
        xoManager.currentTransaction().commit();
    }
}
