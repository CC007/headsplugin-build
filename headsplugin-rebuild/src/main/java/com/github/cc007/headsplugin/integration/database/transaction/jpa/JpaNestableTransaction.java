package com.github.cc007.headsplugin.integration.database.transaction.jpa;

import com.github.cc007.headsplugin.api.business.domain.exceptions.LockingException;
import com.github.cc007.headsplugin.integration.database.transaction.Transaction;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.val;

import javax.persistence.EntityManager;
import javax.persistence.RollbackException;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class JpaNestableTransaction implements Transaction {

    private final EntityManager entityManager;
    private final Thread mainThread;
    private final LockManager lockManager = new LockManager();

    /**
     * Depth of the transaction to be able to handle nested transactions.
     * It will make sure that the actual entity manager's transaction is only committed
     * once the outer most of the nested transactions is committed (eg when the depth is 1).
     */
    private int depth = 0;

    /**
     * To keep track of if any nested transactions want to clear the entity cache, this is tracked here.
     */
    private boolean clearCache = false;

    /**
     * Stack of exception handlers
     */
    private Stack<Consumer<RollbackException>> exceptionHandlerStack = Stack.fromDeque(new LinkedList<>());
    private LockManager.Lock lock;


    @Override
    public <T> T runTransacted(Supplier<T> supplier) {
        return runTransacted(supplier, e -> {
            throw e;
        }, true);
    }

    @Override
    public <T> T runTransacted(Supplier<T> supplier, boolean clearCache) {
        return runTransacted(supplier, e -> {
            throw e;
        }, clearCache);
    }

    @Override
    public <T> T runTransacted(Supplier<T> supplier, Function<RollbackException, T> exceptionHandler) {
        return runTransacted(supplier, exceptionHandler, true);
    }

    @Override
    public <T> T runTransacted(Supplier<T> supplier, Function<RollbackException, T> exceptionHandler, boolean clearCache) {
        final var value = new AtomicReference<T>();
        //noinspection CodeBlock2Expr
        runTransacted(
                () -> {
                    value.set(supplier.get());
                },
                e -> value.set(exceptionHandler.apply(e)),
                clearCache
        );
        return value.get();
    }

    @Override
    public void runTransacted(Runnable runnable) {
        runTransacted(runnable, e -> {
            throw e;
        }, true);
    }

    @Override
    public void runTransacted(Runnable runnable, boolean clearCache) {
        runTransacted(runnable, e -> {
            throw e;
        }, clearCache);
    }

    @Override
    public void runTransacted(Runnable runnable, Consumer<RollbackException> exceptionHandler) {
        runTransacted(runnable, exceptionHandler, true);
    }

    @Override
    public void runTransacted(Runnable runnable, Consumer<RollbackException> exceptionHandler, boolean clearCache) {
        if (Thread.currentThread().equals(mainThread)) {
            lock = lockManager.tryAcquiringLock().orElseThrow(LockingException::new);
        } else {
            lock = lockManager.acquireLock();
        }
        try {
            exceptionHandlerStack.push(exceptionHandler);
            begin();
            runnable.run();
            commit(clearCache);
        } catch (RollbackException e) {
            handleExceptions(e);
        } finally {
            lock.release();
        }
    }

    private void begin() throws LockingException {
        if (depth == 0) {
            entityManager.getTransaction().begin();
        }
        depth++;
    }

    private void commit(boolean clearCache) {
        this.clearCache |= clearCache;
        depth--;
        if (depth == 0) {
            entityManager.getTransaction().commit();
            exceptionHandlerStack.clear();
            if (this.clearCache) {
                entityManager.clear();
                this.clearCache = false;
            }
        }
    }

    private void handleExceptions(RollbackException e) {
        if (!exceptionHandlerStack.isEmpty()) {
            try {
                exceptionHandlerStack.pop().accept(e);
            } catch (RollbackException e2) {
                if (!e2.equals(e)) {
                    e2.addSuppressed(e);
                    e = e2;
                }
                if (exceptionHandlerStack.isEmpty()) {
                    throw e;
                }
            } finally {
                handleExceptions(e);
            }
        }
    }

    private interface Stack<E> {
        void push(E e);

        E pop();

        void clear();

        boolean isEmpty();

        static <E> Stack<E> fromDeque(Deque<E> deque) {
            return new Stack<E>() {
                @Override
                public void push(E e) {
                    deque.push(e);
                }

                @Override
                public E pop() {
                    return deque.pop();
                }

                @Override
                public void clear() {
                    deque.clear();
                }

                @Override
                public boolean isEmpty() {
                    return deque.isEmpty();
                }
            };
        }
    }

}

class LockManager {
    private final ReentrantLock lockContainer = new ReentrantLock();
    private final Lock lock = new Lock(lockContainer::unlock);

    public Lock acquireLock() {
        lockContainer.lock();
        return lock;
    }

    public Optional<Lock> tryAcquiringLock() {
        val lockAquired = lockContainer.tryLock();
        return Optional.of(lock).filter(l -> lockAquired);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    static class Lock {
        private final Runnable lockReleaser;

        public void release() {
            lockReleaser.run();
        }
    }
}
