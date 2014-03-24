package com.buschmais.xo.neo4j.test.validation;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.annotation.PreUpdate;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractCdoManagerTest;
import com.buschmais.xo.neo4j.test.validation.composite.A;
import com.buschmais.xo.neo4j.test.validation.composite.B;
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

    public ValidationTest(XOUnit XOUnit) {
        super(XOUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getCdoUnits() throws URISyntaxException {
        return cdoUnits(A.class, B.class);
    }

    @Test
    public void validationOnCommitAfterInsert() {
        XOManager XOManager = getXOManager();
        XOManager.currentTransaction().begin();
        A a = XOManager.create(A.class);
        Set<ConstraintViolation<?>> constraintViolations = null;
        try {
            XOManager.currentTransaction().commit();
            Assert.fail("Validation must fail.");
        } catch (ConstraintViolationException e) {
            constraintViolations = e.getConstraintViolations();
        }
        assertThat(constraintViolations.size(), equalTo(2));
        B b = XOManager.create(B.class);
        a.setB(b);
        a.setName("Indiana Jones");
        XOManager.currentTransaction().commit();
    }

    @Test
    public void validationOnCommitAfterQuery() {
        XOManager XOManager = getXOManager();
        XOManager.currentTransaction().begin();
        B b = XOManager.create(B.class);
        for (int i = 0; i < 2; i++) {
            A a = XOManager.create(A.class);
            a.setName("Miller");
            a.setB(b);
        }
        XOManager.currentTransaction().commit();
        closeCdoManager();
        XOManager = getXOManager();
        XOManager.currentTransaction().begin();
        for (A miller : XOManager.find(A.class, "Miller")) {
            miller.setName(null);
        }
        Set<ConstraintViolation<?>> constraintViolations = null;
        try {
            XOManager.currentTransaction().commit();
            Assert.fail("Validation must fail.");
        } catch (ConstraintViolationException e) {
            constraintViolations = e.getConstraintViolations();
        }
        assertThat(constraintViolations.size(), equalTo(1));
        XOManager.currentTransaction().rollback();
    }

    @Test
    public void validationAfterPreUpdate() {
        XOManager XOManager = getXOManager();
        XOManager.currentTransaction().begin();
        B b = XOManager.create(B.class);
        A a = XOManager.create(A.class);
        a.setB(b);
        Set<ConstraintViolation<?>> constraintViolations = null;
        try {
            XOManager.currentTransaction().commit();
        } catch (ConstraintViolationException e) {
            constraintViolations = e.getConstraintViolations();
        }
        assertThat(constraintViolations.size(), equalTo(1));
        XOManager.registerInstanceListener(new InstanceListener());
        XOManager.currentTransaction().commit();
    }

    public static final class InstanceListener {
        @PreUpdate
        public void setName(A instance) {
            instance.setName("Miller");
        }
    }
}
