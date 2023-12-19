package com.github.cc007.headsplugin.api.business.services;

import org.apache.logging.log4j.Level;

import java.util.function.Supplier;

public interface Profiler {

    <T> T runProfiled(Supplier<T> supplier);
    <T> T runProfiled(String doneMessage, Supplier<T> supplier);
    <T> T runProfiled(Level logLevel, Supplier<T> supplier);
    <T> T runProfiled(Level logLevel, String doneMessage, Supplier<T> supplier);

    double runProfiled(Runnable runnable);
    double runProfiled(String doneMessage, Runnable runnable);
    double runProfiled(Level logLevel, Runnable runnable);
    double runProfiled(Level logLevel, String doneMessage, Runnable runnable);
}
