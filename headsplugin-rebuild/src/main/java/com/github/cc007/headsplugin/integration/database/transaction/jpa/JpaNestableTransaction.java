package com.github.cc007.headsplugin.integration.database.transaction.jpa;

import com.github.cc007.headsplugin.integration.database.transaction.Transaction;

import lombok.RequiredArgsConstructor;

import javax.persistence.EntityManager;
import javax.persistence.RollbackException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class JpaNestableTransaction implements Transaction {

    private final EntityManager entityManager;

    /**
     * Depth of the transaction to be able to handle nested transactions.
     * It will make sure that the actual entity manager's transaction is only committed
     * once the outer most of the nested transactions is committed (eg when the depth is 1).
     */
    private int depth = 0;

    @Override
    public void runTransacted(Runnable runnable) {
        runTransacted(runnable, (e) -> {throw e;}, true);
    }

    @Override
    public void runTransacted(Runnable runnable, boolean clearCache) {
        runTransacted(runnable, (e) -> {throw e;}, clearCache);
    }

    @Override
    public void runTransacted(Runnable runnable, Consumer<RollbackException> exceptionHandler) {
        runTransacted(runnable, exceptionHandler, true);
    }

    @Override
    public void runTransacted(Runnable runnable, Consumer<RollbackException> exceptionHandler, boolean clearCache) {
        try {
            begin();
            runnable.run();
            commit(clearCache);
        } catch (RollbackException e) {
            exceptionHandler.accept(e);
        }
    }

    @Override
    public <T> T runTransacted(Supplier<T> supplier) {
        return runTransacted(supplier, (e) -> {throw e;}, true);
    }

    @Override
    public <T> T runTransacted(Supplier<T> supplier, boolean clearCache) {
        return runTransacted(supplier, (e) -> {throw e;}, clearCache);
    }

    @Override
    public <T> T runTransacted(Supplier<T> supplier, Consumer<RollbackException> exceptionHandler) {
        return runTransacted(supplier, exceptionHandler, true);
    }

    @Override
    public <T> T runTransacted(Supplier<T> supplier, Consumer<RollbackException> exceptionHandler, boolean clearCache) {
        AtomicReference<T> value = new AtomicReference<>();
        runTransacted(() -> value.set(supplier.get()), (e) -> {throw e;}, clearCache);
        return value.get();
    }

    private void begin() {
        if (depth == 0) {
            entityManager.getTransaction().begin();
        }
        depth++;
    }

    private void commit(boolean clearCache) {
        if (depth == 1) {
            entityManager.getTransaction().commit();
            if (clearCache) {
                entityManager.clear();
            }
        } else if (depth == 0) {
            throw new IllegalStateException("No transaction is currently running!");
        }
        depth--;
    }

}
