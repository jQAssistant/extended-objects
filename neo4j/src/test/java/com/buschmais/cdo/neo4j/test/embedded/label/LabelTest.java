package com.buschmais.cdo.neo4j.test.embedded.label;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.neo4j.test.embedded.AbstractEmbeddedCdoManagerTest;
import com.buschmais.cdo.neo4j.test.embedded.label.composite.ExplicitLabel;
import com.buschmais.cdo.neo4j.test.embedded.label.composite.ImplicitLabel;
import org.junit.Test;

import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertThat;

public class LabelTest extends AbstractEmbeddedCdoManagerTest {

    @Override
    protected Class<?>[] getTypes() {
        return new Class<?>[]{ImplicitLabel.class, ExplicitLabel.class};
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
