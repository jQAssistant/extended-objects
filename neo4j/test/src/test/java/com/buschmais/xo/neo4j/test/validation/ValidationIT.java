package com.buschmais.xo.neo4j.test.validation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.annotation.PreUpdate;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractNeo4JXOManagerIT;
import com.buschmais.xo.neo4j.test.validation.composite.A;
import com.buschmais.xo.neo4j.test.validation.composite.B;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class ValidationIT extends AbstractNeo4JXOManagerIT {

    public ValidationIT(XOUnit xoUnit) {
        super(xoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getXOUnits() {
        return xoUnits(A.class, B.class);
    }

    @Test
    public void validationOnCommitAfterInsert() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction().begin();
        A a = xoManager.create(A.class);
        Set<ConstraintViolation<?>> constraintViolations = null;
        try {
            xoManager.currentTransaction().commit();
            fail("Validation must fail.");
        } catch (ConstraintViolationException e) {
            constraintViolations = e.getConstraintViolations();
        }
        assertThat(constraintViolations.size(), equalTo(2));
        B b = xoManager.create(B.class);
        a.setB(b);
        a.setName("Indiana Jones");
        xoManager.currentTransaction().commit();
    }

    @Test
    public void validationOnCommitAfterQuery() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction().begin();
        B b = xoManager.create(B.class);
        for (int i = 0; i < 2; i++) {
            A a = xoManager.create(A.class);
            a.setName("Miller");
            a.setB(b);
        }
        xoManager.currentTransaction().commit();
        closeXOmanager();
        xoManager = getXOManager();
        xoManager.currentTransaction().begin();
        for (A miller : xoManager.find(A.class, "Miller")) {
            miller.setName(null);
        }
        Set<ConstraintViolation<?>> constraintViolations = null;
        try {
            xoManager.currentTransaction().commit();
            fail("Validation must fail.");
        } catch (ConstraintViolationException e) {
            constraintViolations = e.getConstraintViolations();
        }
        assertThat(constraintViolations.size(), equalTo(1));
        xoManager.currentTransaction().rollback();
    }

    @Test
    public void validationAfterPreUpdate() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction().begin();
        B b = xoManager.create(B.class);
        A a = xoManager.create(A.class);
        a.setB(b);
        Set<ConstraintViolation<?>> constraintViolations = null;
        try {
            xoManager.currentTransaction().commit();
        } catch (ConstraintViolationException e) {
            constraintViolations = e.getConstraintViolations();
        }
        assertThat(constraintViolations.size(), equalTo(1));
        xoManager.registerInstanceListener(new InstanceListener());
        xoManager.currentTransaction().commit();
    }

    public static final class InstanceListener {
        @PreUpdate
        public void setName(A instance) {
            instance.setName("Miller");
        }
    }
}
