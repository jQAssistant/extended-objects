package com.buschmais.cdo.api;

import java.lang.reflect.Method;

public class CdoMandatoryTxException extends CdoException {

    public CdoMandatoryTxException(Method calledMethod) {
        super("An active transaction is MANDATORY when calling method '" + calledMethod.getDeclaringClass().getName() + "#" + calledMethod.getName() + "'");
    }
}
