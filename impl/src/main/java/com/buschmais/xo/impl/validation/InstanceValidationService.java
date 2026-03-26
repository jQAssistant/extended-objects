package com.buschmais.xo.impl.validation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import com.buschmais.xo.impl.cache.TransactionalCache;

import static java.util.Collections.emptySet;

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
            return emptySet();
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
        return validator != null ? validator.validate(instance) : emptySet();
    }

    private Validator getValidator() {
        return validatorFactory != null ? validatorFactory.getValidator() : null;
    }

}
