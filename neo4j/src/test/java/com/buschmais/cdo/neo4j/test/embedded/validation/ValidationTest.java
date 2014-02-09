package com.buschmais.cdo.neo4j.test.embedded.validation;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.neo4j.test.embedded.AbstractEmbeddedCdoManagerTest;
import com.buschmais.cdo.neo4j.test.embedded.validation.composite.A;
import com.buschmais.cdo.neo4j.test.embedded.validation.composite.B;
import org.junit.Assert;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class ValidationTest extends AbstractEmbeddedCdoManagerTest {

    @Override
    protected Class<?>[] getTypes() {
        return new Class<?>[]{A.class, B.class};
    }

    @Test
    public void validationOnCommitAfterInsert() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        A a = cdoManager.create(A.class);
        Set<ConstraintViolation<?>> constraintViolations = null;
        try {
            cdoManager.currentTransaction().commit();
            Assert.fail("Validation must fail.");
        } catch (ConstraintViolationException e) {
            constraintViolations = e.getConstraintViolations();
        }
        assertThat(constraintViolations.size(), equalTo(2));
        B b = cdoManager.create(B.class);
        a.setB(b);
        a.setName("Indiana Jones");
        cdoManager.currentTransaction().commit();
    }

    @Test
    public void validationOnCommitAfterQuery() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        B b = cdoManager.create(B.class);
        for (int i = 0; i < 100; i++) {
            A a = cdoManager.create(A.class);
            a.setName("Miller");
            a.setB(b);
        }
        cdoManager.currentTransaction().commit();
        closeCdoManager();
        cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        for (A miller : cdoManager.find(A.class, "Miller")) {
            miller.setName(null);
        }
        Set<ConstraintViolation<?>> constraintViolations = null;
        try {
            cdoManager.currentTransaction().commit();
            Assert.fail("Validation must fail.");
        } catch (ConstraintViolationException e) {
            constraintViolations = e.getConstraintViolations();
        }
        assertThat(constraintViolations.size(), equalTo(100));
        cdoManager.currentTransaction().rollback();
    }

}
