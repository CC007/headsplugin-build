package com.github.cc007.headsplugin.integration.rest.feign.decoders.statuscode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(StatusCodeHandlers.class)
public @interface StatusCodeHandler {
    int statusCode();

    Class<?> returnType();
}
