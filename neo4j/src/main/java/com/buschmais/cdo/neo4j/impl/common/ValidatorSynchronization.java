package com.buschmais.cdo.neo4j.impl.common;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.api.CdoTransaction;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

public class ValidatorSynchronization implements CdoTransaction.Synchronization{

    private InstanceValidator instanceValidator;

    public ValidatorSynchronization(InstanceValidator instanceValidator) {
        this.instanceValidator = instanceValidator;
    }

    @Override
    public void beforeCompletion() {
        Set<ConstraintViolation<Object>> constraintViolations = instanceValidator.validate();
        if (!constraintViolations.isEmpty()) {
            throw new ConstraintViolationException(constraintViolations);
        }    }

    @Override
    public void afterCompletion(boolean committed) {
    }
}
