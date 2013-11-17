package com.buschmais.cdo.neo4j.api.annotation;

import com.buschmais.cdo.neo4j.api.proxy.NodeProxyMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ImplementedBy {

    Class<? extends NodeProxyMethod> value();

}
