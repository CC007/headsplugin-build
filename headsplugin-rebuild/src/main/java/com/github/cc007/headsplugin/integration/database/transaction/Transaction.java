package com.github.cc007.headsplugin.integration.database.transaction;

import javax.persistence.RollbackException;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface Transaction {
    <T> T runTransacted(Supplier<T> supplier);
    <T> T runTransacted(Supplier<T> supplier, boolean clearCache);
    <T> T runTransacted(Supplier<T> supplier, Consumer<RollbackException> exceptionHandler);
    <T> T runTransacted(Supplier<T> supplier, Consumer<RollbackException> exceptionHandler, boolean clearCache);

    void runTransacted(Runnable runnable);
    void runTransacted(Runnable runnable, boolean clearCache);
    void runTransacted(Runnable runnable, Consumer<RollbackException> exceptionHandler);
    void runTransacted(Runnable runnable, Consumer<RollbackException> exceptionHandler, boolean clearCache);
}
