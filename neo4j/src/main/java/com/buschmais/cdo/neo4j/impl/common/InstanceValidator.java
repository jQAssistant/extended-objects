package com.buschmais.cdo.neo4j.impl.common;

import com.buschmais.cdo.impl.cache.TransactionalCache;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class InstanceValidator {

    private final ValidatorFactory validatorFactory;

    private final TransactionalCache<?> cache;

    public InstanceValidator(ValidatorFactory validatorFactory, TransactionalCache<?> cache) {
        this.validatorFactory = validatorFactory;
        this.cache = cache;
    }

    public Set<ConstraintViolation<Object>> validate() {
        if (validatorFactory == null) {
            return Collections.emptySet();
        }
        Validator validator = validatorFactory.getValidator();
        Set<ConstraintViolation<Object>> violations = new HashSet<>();
        for (Object instance : new ArrayList(cache.values())) {
            violations.addAll(validator.validate(instance));
        }
        return violations;

    }
}
