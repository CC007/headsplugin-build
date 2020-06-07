package com.github.cc007.headsplugin.config.aspects.profiler;

import org.slf4j.event.Level;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Profiler {
    String message() default "Done";

    Level logLevel() default Level.DEBUG;
}
