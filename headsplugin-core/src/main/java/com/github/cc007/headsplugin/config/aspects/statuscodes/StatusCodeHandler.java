package com.github.cc007.headsplugin.config.aspects.statuscodes;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(StatusCodeHandlers.class)
public @interface StatusCodeHandler {
    int statusCode();

    Class returnType();
}
