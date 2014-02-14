package com.buschmais.cdo.neo4j.test.validation;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.api.annotation.PreUpdate;
import com.buschmais.cdo.api.bootstrap.CdoUnit;
import com.buschmais.cdo.neo4j.test.AbstractCdoManagerTest;
import com.buschmais.cdo.neo4j.test.validation.composite.A;
import com.buschmais.cdo.neo4j.test.validation.composite.B;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Set;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class ValidationTest extends AbstractCdoManagerTest {

    public ValidationTest(CdoUnit cdoUnit) {
        super(cdoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getCdoUnits() throws URISyntaxException {
        return cdoUnits(A.class, B.class);
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
        for (int i = 0; i < 2; i++) {
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
        assertThat(constraintViolations.size(), equalTo(1));
        cdoManager.currentTransaction().rollback();
    }

    @Test
    public void validationAfterPreUpdate() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        B b = cdoManager.create(B.class);
        A a = cdoManager.create(A.class);
        a.setB(b);
        Set<ConstraintViolation<?>> constraintViolations = null;
        try {
            cdoManager.currentTransaction().commit();
        } catch (ConstraintViolationException e) {
            constraintViolations = e.getConstraintViolations();
        }
        assertThat(constraintViolations.size(), equalTo(1));
        cdoManager.registerInstanceListener(new InstanceListener());
        cdoManager.currentTransaction().commit();
    }

    public static final class InstanceListener {
        @PreUpdate
        public void setName(A instance) {
            instance.setName("Miller");
        }
    }
}
