package com.buschmais.cdo.neo4j.api.annotation;

import com.buschmais.cdo.neo4j.api.proxy.ProxyMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface InvokeUsing {

    Class<? extends ProxyMethod> value();

}
