package com.buschmais.cdo.impl.validation;

import com.buschmais.cdo.impl.cache.TransactionalCache;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class InstanceValidationService {

    private final ValidatorFactory validatorFactory;

    private final TransactionalCache<?>[] caches;

    public InstanceValidationService(ValidatorFactory validatorFactory, TransactionalCache<?>... caches) {
        this.validatorFactory = validatorFactory;
        this.caches = caches;
    }

    public Set<ConstraintViolation<Object>> validate() {
        Validator validator = getValidator();
        if (validator == null) {
            return Collections.emptySet();
        }
        Set<ConstraintViolation<Object>> violations = new HashSet<>();
        for (TransactionalCache<?> cache : caches) {
            for (Object instance : new ArrayList<>(cache.writtenInstances())) {
                violations.addAll(validator.validate(instance));
            }
        }
        return violations;
    }

    public Set<ConstraintViolation<Object>> validate(Object instance) {
        Validator validator = getValidator();
        return validator != null ? validator.validate(instance) : Collections.<ConstraintViolation<Object>>emptySet();
    }

    private Validator getValidator() {
        return validatorFactory != null ? validatorFactory.getValidator() : null;
    }

}
