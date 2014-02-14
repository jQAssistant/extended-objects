package com.buschmais.cdo.neo4j.test.label;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.api.bootstrap.CdoUnit;
import com.buschmais.cdo.neo4j.test.AbstractCdoManagerTest;
import com.buschmais.cdo.neo4j.test.label.composite.ExplicitLabel;
import com.buschmais.cdo.neo4j.test.label.composite.ImplicitLabel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.net.URISyntaxException;
import java.util.Collection;

import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class LabelTest extends AbstractCdoManagerTest {

    public LabelTest(CdoUnit cdoUnit) {
        super(cdoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getCdoUnits() throws URISyntaxException {
        return cdoUnits(ImplicitLabel.class, ExplicitLabel.class);
    }

    @Test
    public void implicitLabel() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        ImplicitLabel implicitLabel = cdoManager.create(ImplicitLabel.class);
        assertThat(executeQuery("MATCH (n:ImplicitLabel) RETURN n").getColumn("n"), hasItem(implicitLabel));
        cdoManager.currentTransaction().commit();
    }


    @Test
    public void explicitLabel() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        ExplicitLabel explicitLabel = cdoManager.create(ExplicitLabel.class);
        assertThat(executeQuery("MATCH (n:EXPLICIT_LABEL) RETURN n").getColumn("n"), hasItem(explicitLabel));
        cdoManager.currentTransaction().commit();
    }
}
