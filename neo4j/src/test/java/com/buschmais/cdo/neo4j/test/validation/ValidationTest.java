package com.buschmais.cdo.neo4j.test.validation;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.neo4j.test.AbstractCdoManagerTest;
import com.buschmais.cdo.neo4j.test.validation.composite.A;
import com.buschmais.cdo.neo4j.test.validation.composite.B;
import org.junit.Assert;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class ValidationTest extends AbstractCdoManagerTest {

    @Override
    protected Class<?>[] getTypes() {
        return new Class<?>[]{A.class, B.class};
    }

    @Test
    public void validationOnCommitAfterInsert() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.begin();
        A a = cdoManager.create(A.class);
        Set<ConstraintViolation<?>> constraintViolations = null;
        try {
            cdoManager.commit();
            Assert.fail("Validation must fail.");
        } catch (ConstraintViolationException e) {
            constraintViolations = e.getConstraintViolations();
        }
        assertThat(constraintViolations.size(), equalTo(2));
        B b = cdoManager.create(B.class);
        a.setB(b);
        a.setName("Indiana Jones");
        cdoManager.commit();
    }

    @Test
    public void validationOnCommitAfterQuery() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.begin();
        B b = cdoManager.create(B.class);
        for (int i = 0; i < 100; i++) {
            A a = cdoManager.create(A.class);
            a.setName("Miller");
            a.setB(b);
        }
        cdoManager.commit();
        closeCdoManager();
        cdoManager.begin();
        for (A miller : cdoManager.find(A.class, "Miller")) {
            miller.setName(null);
        }
        Set<ConstraintViolation<?>> constraintViolations = null;
        try {
            cdoManager.commit();
            Assert.fail("Validation must fail.");
        } catch (ConstraintViolationException e) {
            constraintViolations = e.getConstraintViolations();
        }
        assertThat(constraintViolations.size(), equalTo(100));
        cdoManager.rollback();
    }

}
