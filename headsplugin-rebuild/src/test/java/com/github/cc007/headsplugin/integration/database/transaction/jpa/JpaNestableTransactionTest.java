package com.github.cc007.headsplugin.integration.database.transaction.jpa;

import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.RollbackException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.emptyArray;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JpaNestableTransactionTest {

    private static final String RETURN_VALUE = "Hello World!";

    @Mock
    EntityManager entityManager;

    @InjectMocks
    JpaNestableTransaction jpaNestableTransaction;

    @Test
    void runTransacted() {
        // prepare
        val runnable = mock(Runnable.class);
        val entityTransaction = mock(EntityTransaction.class);

        when(entityManager.getTransaction())
                .thenReturn(entityTransaction, entityTransaction);
        doNothing().when(entityTransaction).begin();
        doNothing().when(runnable).run();
        doNothing().when(entityTransaction).commit();
        doNothing().when(entityManager).clear();

        // execute
        jpaNestableTransaction.runTransacted(runnable);

        // verify
        verify(entityTransaction).begin();
        verify(entityTransaction).commit();
        verify(runnable).run();
        verify(entityManager, times(2)).getTransaction();
        verify(entityManager).clear();
        verifyNoMoreInteractions(entityManager, runnable, entityTransaction);
    }

    @Test
    void runTransactedWithRollbackException() {
        // prepare
        val runnable = mock(Runnable.class);
        val entityTransaction = mock(EntityTransaction.class);
        var testRollbackException = new RollbackException();

        when(entityManager.getTransaction())
                .thenReturn(entityTransaction, entityTransaction);
        doNothing().when(entityTransaction).begin();
        doNothing().when(runnable).run();
        doThrow(testRollbackException).when(entityTransaction).commit();

        // execute
        Assertions.assertThrows(RollbackException.class, () ->
                jpaNestableTransaction.runTransacted(runnable)
        );

        // verify
        verify(entityTransaction).begin();
        verify(entityTransaction).commit();
        verify(runnable).run();
        verify(entityManager, times(2)).getTransaction();
        verifyNoMoreInteractions(entityManager, runnable, entityTransaction);
    }

    @Test
    void runTransactedNested() {
        // prepare
        val runnable = mock(Runnable.class);
        val entityTransaction = mock(EntityTransaction.class);

        when(entityManager.getTransaction())
                .thenReturn(entityTransaction, entityTransaction);
        doNothing().when(entityTransaction).begin();
        doNothing().when(runnable).run();
        doNothing().when(entityTransaction).commit();
        doNothing().when(entityManager).clear();

        // execute
        //noinspection CodeBlock2Expr
        jpaNestableTransaction.runTransacted(() -> {
            jpaNestableTransaction.runTransacted(runnable);
        });

        // verify
        verify(entityTransaction).begin();
        verify(entityTransaction).commit();
        verify(runnable).run();
        verify(entityManager, times(2)).getTransaction();
        verify(entityManager).clear();
        verifyNoMoreInteractions(entityManager, runnable, entityTransaction);
    }

    @Test
    void runTransactedClearCacheTrue() {
        // prepare
        val runnable = mock(Runnable.class);
        val entityTransaction = mock(EntityTransaction.class);

        when(entityManager.getTransaction())
                .thenReturn(entityTransaction, entityTransaction);
        doNothing().when(entityTransaction).begin();
        doNothing().when(runnable).run();
        doNothing().when(entityTransaction).commit();
        doNothing().when(entityManager).clear();

        // execute
        jpaNestableTransaction.runTransacted(runnable, true);

        // verify
        verify(entityTransaction).begin();
        verify(entityTransaction).commit();
        verify(runnable).run();
        verify(entityManager, times(2)).getTransaction();
        verify(entityManager).clear();
        verifyNoMoreInteractions(entityManager, runnable, entityTransaction);
    }

    @Test
    void runTransactedClearCacheFalse() {
        // prepare
        val runnable = mock(Runnable.class);
        val entityTransaction = mock(EntityTransaction.class);

        when(entityManager.getTransaction())
                .thenReturn(entityTransaction, entityTransaction);
        doNothing().when(entityTransaction).begin();
        doNothing().when(runnable).run();
        doNothing().when(entityTransaction).commit();

        // execute
        jpaNestableTransaction.runTransacted(runnable, false);

        // verify
        verify(entityTransaction).begin();
        verify(entityTransaction).commit();
        verify(runnable).run();
        verify(entityManager, times(2)).getTransaction();
        verifyNoMoreInteractions(entityManager, runnable, entityTransaction);
    }

    @Test
    void runTransactedClearCacheTrueThenFalse() {
        // prepare
        val runnable = mock(Runnable.class);
        val entityTransaction = mock(EntityTransaction.class);

        when(entityManager.getTransaction())
                .thenReturn(entityTransaction, entityTransaction);
        doNothing().when(entityTransaction).begin();
        doNothing().when(runnable).run();
        doNothing().when(entityTransaction).commit();
        doNothing().when(entityManager).clear();

        // execute
        jpaNestableTransaction.runTransacted(runnable, true);
        jpaNestableTransaction.runTransacted(runnable, false);

        // verify
        verify(entityTransaction, times(2)).begin();
        verify(entityTransaction, times(2)).commit();
        verify(runnable, times(2)).run();
        verify(entityManager, times(4)).getTransaction();
        verify(entityManager).clear(); // <- only once!
        verifyNoMoreInteractions(entityManager, runnable, entityTransaction);
    }

    @Test
    void runTransactedClearCacheTrueWithRollbackException() {
        // prepare
        val runnable = mock(Runnable.class);
        val entityTransaction = mock(EntityTransaction.class);
        var testRollbackException = new RollbackException();

        when(entityManager.getTransaction())
                .thenReturn(entityTransaction, entityTransaction);
        doNothing().when(entityTransaction).begin();
        doNothing().when(runnable).run();
        doThrow(testRollbackException).when(entityTransaction).commit();

        // execute
        Assertions.assertThrows(RollbackException.class, () ->
                jpaNestableTransaction.runTransacted(runnable, true)
        );

        // verify
        verify(entityTransaction).begin();
        verify(entityTransaction).commit();
        verify(runnable).run();
        verify(entityManager, times(2)).getTransaction();
        verifyNoMoreInteractions(entityManager, runnable, entityTransaction);
    }

    @Test
    void runTransactedClearCacheFalseWithRollbackException() {
        // prepare
        val runnable = mock(Runnable.class);
        val entityTransaction = mock(EntityTransaction.class);
        var testRollbackException = new RollbackException();

        when(entityManager.getTransaction())
                .thenReturn(entityTransaction, entityTransaction);
        doNothing().when(entityTransaction).begin();
        doNothing().when(runnable).run();
        doThrow(testRollbackException).when(entityTransaction).commit();

        // execute
        Assertions.assertThrows(RollbackException.class, () ->
                jpaNestableTransaction.runTransacted(runnable, false)
        );

        // verify
        verify(entityTransaction).begin();
        verify(entityTransaction).commit();
        verify(runnable).run();
        verify(entityManager, times(2)).getTransaction();
        verifyNoMoreInteractions(entityManager, runnable, entityTransaction);
    }

    @Test
    void runTransactedClearCacheTrueNested() {
        // prepare
        val runnable = mock(Runnable.class);
        val entityTransaction = mock(EntityTransaction.class);

        when(entityManager.getTransaction())
                .thenReturn(entityTransaction, entityTransaction);
        doNothing().when(entityTransaction).begin();
        doNothing().when(runnable).run();
        doNothing().when(entityTransaction).commit();
        doNothing().when(entityManager).clear();

        // execute
        //noinspection CodeBlock2Expr
        jpaNestableTransaction.runTransacted(() -> {
            jpaNestableTransaction.runTransacted(runnable, true);
        }, true);

        // verify
        verify(entityTransaction).begin();
        verify(entityTransaction).commit();
        verify(runnable).run();
        verify(entityManager, times(2)).getTransaction();
        verify(entityManager).clear();
        verifyNoMoreInteractions(entityManager, runnable, entityTransaction);
    }

    @Test
    void runTransactedClearCacheFalseTrueNested() {
        // prepare
        val runnable = mock(Runnable.class);
        val entityTransaction = mock(EntityTransaction.class);

        when(entityManager.getTransaction())
                .thenReturn(entityTransaction, entityTransaction);
        doNothing().when(entityTransaction).begin();
        doNothing().when(runnable).run();
        doNothing().when(entityTransaction).commit();

        // execute
        //noinspection CodeBlock2Expr
        jpaNestableTransaction.runTransacted(() -> {
            jpaNestableTransaction.runTransacted(runnable, false);
        }, false);

        // verify
        verify(entityTransaction).begin();
        verify(entityTransaction).commit();
        verify(runnable).run();
        verify(entityManager, times(2)).getTransaction();
        verifyNoMoreInteractions(entityManager, runnable, entityTransaction);
    }

    @Test
    void runTransactedClearCacheOuterTrueNested() {
        // prepare
        val runnable = mock(Runnable.class);
        val entityTransaction = mock(EntityTransaction.class);

        when(entityManager.getTransaction())
                .thenReturn(entityTransaction, entityTransaction);
        doNothing().when(entityTransaction).begin();
        doNothing().when(runnable).run();
        doNothing().when(entityTransaction).commit();
        doNothing().when(entityManager).clear();

        // execute
        //noinspection CodeBlock2Expr
        jpaNestableTransaction.runTransacted(() -> {
            jpaNestableTransaction.runTransacted(runnable, false);
        }, true);

        // verify
        verify(entityTransaction).begin();
        verify(entityTransaction).commit();
        verify(runnable).run();
        verify(entityManager, times(2)).getTransaction();
        verify(entityManager).clear();
        verifyNoMoreInteractions(entityManager, runnable, entityTransaction);
    }

    @Test
    void runTransactedClearCacheInnerTrueTrueNested() {
        // prepare
        val runnable = mock(Runnable.class);
        val entityTransaction = mock(EntityTransaction.class);

        when(entityManager.getTransaction())
                .thenReturn(entityTransaction, entityTransaction);
        doNothing().when(entityTransaction).begin();
        doNothing().when(runnable).run();
        doNothing().when(entityTransaction).commit();
        doNothing().when(entityManager).clear();

        // execute
        //noinspection CodeBlock2Expr
        jpaNestableTransaction.runTransacted(() -> {
            jpaNestableTransaction.runTransacted(runnable, true);
        }, false);

        // verify
        verify(entityTransaction).begin();
        verify(entityTransaction).commit();
        verify(runnable).run();
        verify(entityManager, times(2)).getTransaction();
        verify(entityManager).clear();
        verifyNoMoreInteractions(entityManager, runnable, entityTransaction);
    }

    @Test
    void runTransactedWithRollackHandler() {
        // prepare
        val runnable = mock(Runnable.class);
        @SuppressWarnings("unchecked") val exceptionHandler = (Consumer<RollbackException>) mock(Consumer.class);
        val entityTransaction = mock(EntityTransaction.class);

        when(entityManager.getTransaction())
                .thenReturn(entityTransaction, entityTransaction);
        doNothing().when(entityTransaction).begin();
        doNothing().when(runnable).run();
        doNothing().when(entityTransaction).commit();
        doNothing().when(entityManager).clear();

        // execute
        jpaNestableTransaction.runTransacted(runnable, exceptionHandler);

        // verify
        verify(entityTransaction).begin();
        verify(entityTransaction).commit();
        verify(runnable).run();
        verify(entityManager, times(2)).getTransaction();
        verify(entityManager).clear();
        verifyNoMoreInteractions(entityManager, runnable, entityTransaction);
        verifyNoInteractions(exceptionHandler);
    }

    @Test
    void runTransactedWithRollackHandlerThrowingWithRollbackException() {
        // prepare
        val runnable = mock(Runnable.class);
        @SuppressWarnings("unchecked") val exceptionHandler = (Consumer<RollbackException>) mock(Consumer.class);
        val entityTransaction = mock(EntityTransaction.class);
        val testRollbackException = new RollbackException();

        when(entityManager.getTransaction())
                .thenReturn(entityTransaction, entityTransaction);
        doNothing().when(entityTransaction).begin();
        doNothing().when(runnable).run();
        doThrow(testRollbackException).when(entityTransaction).commit();
        doThrow(testRollbackException).when(exceptionHandler).accept(testRollbackException);

        // execute
        Assertions.assertThrows(RollbackException.class, () ->
                jpaNestableTransaction.runTransacted(runnable, exceptionHandler)
        );

        // verify
        verify(entityTransaction).begin();
        verify(entityTransaction).commit();
        verify(runnable).run();
        verify(entityManager, times(2)).getTransaction();
        verifyNoMoreInteractions(entityManager, runnable, entityTransaction, exceptionHandler);
        verifyNoMoreInteractions(exceptionHandler);
    }

    @Test
    void runTransactedWithRollackHandlerDoNothingWithRollbackException() {
        // prepare
        val runnable = mock(Runnable.class);
        @SuppressWarnings("unchecked") val exceptionHandler = (Consumer<RollbackException>) mock(Consumer.class);
        val entityTransaction = mock(EntityTransaction.class);
        val testRollbackException = new RollbackException();

        when(entityManager.getTransaction())
                .thenReturn(entityTransaction, entityTransaction);
        doNothing().when(entityTransaction).begin();
        doNothing().when(runnable).run();
        doThrow(testRollbackException).when(entityTransaction).commit();
        doNothing().when(exceptionHandler).accept(testRollbackException);

        // execute
        jpaNestableTransaction.runTransacted(runnable, exceptionHandler);

        // verify
        verify(entityTransaction).begin();
        verify(entityTransaction).commit();
        verify(runnable).run();
        verify(entityManager, times(2)).getTransaction();
        verifyNoMoreInteractions(entityManager, runnable, entityTransaction, exceptionHandler);
        verifyNoMoreInteractions(exceptionHandler);
    }

    @Test
    void runTransactedWithRollackHandlerNested() {
        // prepare
        val runnable = mock(Runnable.class);
        @SuppressWarnings("unchecked") val exceptionHandler = (Consumer<RollbackException>) mock(Consumer.class);
        val entityTransaction = mock(EntityTransaction.class);

        when(entityManager.getTransaction())
                .thenReturn(entityTransaction, entityTransaction);
        doNothing().when(entityTransaction).begin();
        doNothing().when(runnable).run();
        doNothing().when(entityTransaction).commit();
        doNothing().when(entityManager).clear();

        // execute
        //noinspection CodeBlock2Expr
        jpaNestableTransaction.runTransacted(() -> {
            jpaNestableTransaction.runTransacted(runnable, exceptionHandler);
        }, exceptionHandler);

        // verify
        verify(entityTransaction).begin();
        verify(entityTransaction).commit();
        verify(runnable).run();
        verify(entityManager, times(2)).getTransaction();
        verify(entityManager).clear();
        verifyNoMoreInteractions(entityManager, runnable, entityTransaction);
        verifyNoInteractions(exceptionHandler);
    }

    @Test
    void runTransactedWithRollackHandlerThrowingSameExceptionNestedWithRollbackException() {
        // prepare
        val runnable = mock(Runnable.class);
        @SuppressWarnings("unchecked") val exceptionHandlerInner = (Consumer<RollbackException>) mock(Consumer.class);
        @SuppressWarnings("unchecked") val exceptionHandlerOuter = (Consumer<RollbackException>) mock(Consumer.class);
        val entityTransaction = mock(EntityTransaction.class);
        val testRollbackException = new RollbackException("RBE");

        when(entityManager.getTransaction())
                .thenReturn(entityTransaction, entityTransaction);
        doNothing().when(entityTransaction).begin();
        doNothing().when(runnable).run();
        doThrow(testRollbackException).when(entityTransaction).commit();
        doThrow(testRollbackException).when(exceptionHandlerInner).accept(testRollbackException);
        doThrow(testRollbackException).when(exceptionHandlerOuter).accept(testRollbackException);

        // execute
        //noinspection CodeBlock2Expr
        val actualException = Assertions.assertThrows(RollbackException.class, () ->
                jpaNestableTransaction.runTransacted(() -> {
                    jpaNestableTransaction.runTransacted(runnable, exceptionHandlerInner);
                }, exceptionHandlerOuter)
        );

        // verify
        assertThat(actualException, is(testRollbackException));
        assertThat(actualException.getSuppressed(), emptyArray());
        verify(entityTransaction).begin();
        verify(entityTransaction).commit();
        verify(runnable).run();
        verify(entityManager, times(2)).getTransaction();
        verifyNoMoreInteractions(entityManager, runnable, entityTransaction);
        verifyNoMoreInteractions(exceptionHandlerInner, exceptionHandlerOuter);
    }

    @Test
    void runTransactedWithRollackHandlerInnerThrowingDifferentExceptionNestedWithRollbackException() {
        // prepare
        val runnable = mock(Runnable.class);
        @SuppressWarnings("unchecked") val exceptionHandlerInner = (Consumer<RollbackException>) mock(Consumer.class);
        @SuppressWarnings("unchecked") val exceptionHandlerOuter = (Consumer<RollbackException>) mock(Consumer.class);
        val entityTransaction = mock(EntityTransaction.class);
        val testRollbackException1 = new RollbackException("RBE1");
        val testRollbackException2 = new RollbackException("RBE2");

        when(entityManager.getTransaction())
                .thenReturn(entityTransaction, entityTransaction);
        doNothing().when(entityTransaction).begin();
        doNothing().when(runnable).run();
        doThrow(testRollbackException1).when(entityTransaction).commit();
        doThrow(testRollbackException2).when(exceptionHandlerInner).accept(testRollbackException1);
        doThrow(testRollbackException2).when(exceptionHandlerOuter).accept(testRollbackException2);

        // execute
        //noinspection CodeBlock2Expr
        val actualException = Assertions.assertThrows(RollbackException.class, () ->
                jpaNestableTransaction.runTransacted(() -> {
                    jpaNestableTransaction.runTransacted(runnable, exceptionHandlerInner);
                }, exceptionHandlerOuter)
        );

        // verify
        assertThat(actualException, is(testRollbackException2));
        assertThat(actualException.getSuppressed(), arrayContaining(testRollbackException1));
        verify(entityTransaction).begin();
        verify(entityTransaction).commit();
        verify(runnable).run();
        verify(entityManager, times(2)).getTransaction();
        verifyNoMoreInteractions(entityManager, runnable, entityTransaction);
        verifyNoMoreInteractions(exceptionHandlerInner, exceptionHandlerOuter);
    }

    @Test
    void runTransactedWithRollackHandlerOuterThrowingDifferentExceptionNestedWithRollbackException() {
        // prepare
        val runnable = mock(Runnable.class);
        @SuppressWarnings("unchecked") val exceptionHandlerInner = (Consumer<RollbackException>) mock(Consumer.class);
        @SuppressWarnings("unchecked") val exceptionHandlerOuter = (Consumer<RollbackException>) mock(Consumer.class);
        val entityTransaction = mock(EntityTransaction.class);
        val testRollbackException1 = new RollbackException("RBE1");
        val testRollbackException2 = new RollbackException("RBE2");

        when(entityManager.getTransaction())
                .thenReturn(entityTransaction, entityTransaction);
        doNothing().when(entityTransaction).begin();
        doNothing().when(runnable).run();
        doThrow(testRollbackException1).when(entityTransaction).commit();
        doThrow(testRollbackException1).when(exceptionHandlerInner).accept(testRollbackException1);
        doThrow(testRollbackException2).when(exceptionHandlerOuter).accept(testRollbackException1);

        // execute
        //noinspection CodeBlock2Expr
        val actualException = Assertions.assertThrows(RollbackException.class, () ->
                jpaNestableTransaction.runTransacted(() -> {
                    jpaNestableTransaction.runTransacted(runnable, exceptionHandlerInner);
                }, exceptionHandlerOuter)
        );

        // verify
        assertThat(actualException, is(testRollbackException2));
        assertThat(actualException.getSuppressed(), arrayContaining(testRollbackException1));
        verify(entityTransaction).begin();
        verify(entityTransaction).commit();
        verify(runnable).run();
        verify(entityManager, times(2)).getTransaction();
        verifyNoMoreInteractions(entityManager, runnable, entityTransaction);
        verifyNoMoreInteractions(exceptionHandlerInner, exceptionHandlerOuter);
    }

    @Test
    void runTransactedWithRollackHandlerDoNothingNestedWithRollbackException() {
        // prepare
        val runnable = mock(Runnable.class);
        @SuppressWarnings("unchecked") val exceptionHandlerInner = (Consumer<RollbackException>) mock(Consumer.class);
        @SuppressWarnings("unchecked") val exceptionHandlerOuter = (Consumer<RollbackException>) mock(Consumer.class);
        val entityTransaction = mock(EntityTransaction.class);
        val testRollbackException = new RollbackException();

        when(entityManager.getTransaction())
                .thenReturn(entityTransaction, entityTransaction);
        doNothing().when(entityTransaction).begin();
        doNothing().when(runnable).run();
        doThrow(testRollbackException).when(entityTransaction).commit();
        doNothing().when(exceptionHandlerInner).accept(testRollbackException);
        doNothing().when(exceptionHandlerOuter).accept(testRollbackException);

        // execute
        //noinspection CodeBlock2Expr
        jpaNestableTransaction.runTransacted(() -> {
            jpaNestableTransaction.runTransacted(runnable, exceptionHandlerInner);
        }, exceptionHandlerOuter);

        // verify
        assertThat(testRollbackException.getSuppressed(), emptyArray());
        verify(entityTransaction).begin();
        verify(entityTransaction).commit();
        verify(runnable).run();
        verify(entityManager, times(2)).getTransaction();
        verifyNoMoreInteractions(entityManager, runnable, entityTransaction);
        verifyNoMoreInteractions(exceptionHandlerInner, exceptionHandlerOuter);
    }

    @Test
    void runTransactedWithRollackHandlerInnerDoNothingOuterThrowingSameExceptionNestedWithRollbackException() {
        // prepare
        val runnable = mock(Runnable.class);
        @SuppressWarnings("unchecked") val exceptionHandlerInner = (Consumer<RollbackException>) mock(Consumer.class);
        @SuppressWarnings("unchecked") val exceptionHandlerOuter = (Consumer<RollbackException>) mock(Consumer.class);
        val entityTransaction = mock(EntityTransaction.class);
        val testRollbackException = new RollbackException("RBE1");

        when(entityManager.getTransaction())
                .thenReturn(entityTransaction, entityTransaction);
        doNothing().when(entityTransaction).begin();
        doNothing().when(runnable).run();
        doThrow(testRollbackException).when(entityTransaction).commit();
        doNothing().when(exceptionHandlerInner).accept(testRollbackException);
        doThrow(testRollbackException).when(exceptionHandlerOuter).accept(testRollbackException);

        // execute
        //noinspection CodeBlock2Expr
        val actualException = Assertions.assertThrows(RollbackException.class, () ->
                jpaNestableTransaction.runTransacted(() -> {
                    jpaNestableTransaction.runTransacted(runnable, exceptionHandlerInner);
                }, exceptionHandlerOuter)
        );

        // verify
        assertThat(actualException, is(testRollbackException));
        assertThat(actualException.getSuppressed(), emptyArray());
        verify(entityTransaction).begin();
        verify(entityTransaction).commit();
        verify(runnable).run();
        verify(entityManager, times(2)).getTransaction();
        verifyNoMoreInteractions(entityManager, runnable, entityTransaction);
        verifyNoMoreInteractions(exceptionHandlerInner, exceptionHandlerOuter);
    }

    @Test
    void runTransactedWithRollackHandlerInnerDoNothingOuterThrowingDifferentExceptionNestedWithRollbackException() {
        // prepare
        val runnable = mock(Runnable.class);
        @SuppressWarnings("unchecked") val exceptionHandlerInner = (Consumer<RollbackException>) mock(Consumer.class);
        @SuppressWarnings("unchecked") val exceptionHandlerOuter = (Consumer<RollbackException>) mock(Consumer.class);
        val entityTransaction = mock(EntityTransaction.class);
        val testRollbackException1 = new RollbackException("RBE1");
        val testRollbackException2 = new RollbackException("RBE2");

        when(entityManager.getTransaction())
                .thenReturn(entityTransaction, entityTransaction);
        doNothing().when(entityTransaction).begin();
        doNothing().when(runnable).run();
        doThrow(testRollbackException1).when(entityTransaction).commit();
        doNothing().when(exceptionHandlerInner).accept(testRollbackException1);
        doThrow(testRollbackException2).when(exceptionHandlerOuter).accept(testRollbackException1);

        // execute
        //noinspection CodeBlock2Expr
        val actualException = Assertions.assertThrows(RollbackException.class, () ->
                jpaNestableTransaction.runTransacted(() -> {
                    jpaNestableTransaction.runTransacted(runnable, exceptionHandlerInner);
                }, exceptionHandlerOuter)
        );

        // verify
        assertThat(actualException, is(testRollbackException2));
        assertThat(actualException.getSuppressed(), arrayContaining(testRollbackException1));
        verify(entityTransaction).begin();
        verify(entityTransaction).commit();
        verify(runnable).run();
        verify(entityManager, times(2)).getTransaction();
        verifyNoMoreInteractions(entityManager, runnable, entityTransaction);
        verifyNoMoreInteractions(exceptionHandlerInner, exceptionHandlerOuter);
    }

    @Test
    void runTransactedWithRollackHandlerInnerThrowingSameExceptionOuterDoNothingNestedWithRollbackException() {
        // prepare
        val runnable = mock(Runnable.class);
        @SuppressWarnings("unchecked") val exceptionHandlerInner = (Consumer<RollbackException>) mock(Consumer.class);
        @SuppressWarnings("unchecked") val exceptionHandlerOuter = (Consumer<RollbackException>) mock(Consumer.class);
        val entityTransaction = mock(EntityTransaction.class);
        val testRollbackException = new RollbackException("RBE1");

        when(entityManager.getTransaction())
                .thenReturn(entityTransaction, entityTransaction);
        doNothing().when(entityTransaction).begin();
        doNothing().when(runnable).run();
        doThrow(testRollbackException).when(entityTransaction).commit();
        doThrow(testRollbackException).when(exceptionHandlerInner).accept(testRollbackException);
        doNothing().when(exceptionHandlerOuter).accept(testRollbackException);

        // execute
        //noinspection CodeBlock2Expr
        jpaNestableTransaction.runTransacted(() -> {
            jpaNestableTransaction.runTransacted(runnable, exceptionHandlerInner);
        }, exceptionHandlerOuter);

        // verify
        assertThat(testRollbackException.getSuppressed(), emptyArray());
        verify(entityTransaction).begin();
        verify(entityTransaction).commit();
        verify(runnable).run();
        verify(entityManager, times(2)).getTransaction();
        verifyNoMoreInteractions(entityManager, runnable, entityTransaction);
        verifyNoMoreInteractions(exceptionHandlerInner, exceptionHandlerOuter);
    }

    @Test
    void runTransactedWithRollackHandlerInnerThrowingDifferentExceptionOuterDoNothingNestedWithRollbackException() {
        // prepare
        val runnable = mock(Runnable.class);
        @SuppressWarnings("unchecked") val exceptionHandlerInner = (Consumer<RollbackException>) mock(Consumer.class);
        @SuppressWarnings("unchecked") val exceptionHandlerOuter = (Consumer<RollbackException>) mock(Consumer.class);
        val entityTransaction = mock(EntityTransaction.class);
        val testRollbackException1 = new RollbackException("RBE1");
        val testRollbackException2 = new RollbackException("RBE2");

        when(entityManager.getTransaction())
                .thenReturn(entityTransaction, entityTransaction);
        doNothing().when(entityTransaction).begin();
        doNothing().when(runnable).run();
        doThrow(testRollbackException1).when(entityTransaction).commit();
        doThrow(testRollbackException2).when(exceptionHandlerInner).accept(testRollbackException1);
        doNothing().when(exceptionHandlerOuter).accept(testRollbackException2);

        // execute
        //noinspection CodeBlock2Expr
        jpaNestableTransaction.runTransacted(() -> {
            jpaNestableTransaction.runTransacted(runnable, exceptionHandlerInner);
        }, exceptionHandlerOuter);

        // verify
        assertThat(testRollbackException2.getSuppressed(), arrayContaining(testRollbackException1));
        verify(entityTransaction).begin();
        verify(entityTransaction).commit();
        verify(runnable).run();
        verify(entityManager, times(2)).getTransaction();
        verifyNoMoreInteractions(entityManager, runnable, entityTransaction);
        verifyNoMoreInteractions(exceptionHandlerInner, exceptionHandlerOuter);
    }

    private String supplyValue() {
        return RETURN_VALUE;
    }

    @Test
    void runTransactedReturningValue() {
        // prepare
        @SuppressWarnings("unchecked") val supplier = (Supplier<String>) mock(Supplier.class);
        val entityTransaction = mock(EntityTransaction.class);

        when(entityManager.getTransaction())
                .thenReturn(entityTransaction, entityTransaction);
        doNothing().when(entityTransaction).begin();
        when(supplier.get())
                .thenReturn(supplyValue());
        doNothing().when(entityTransaction).commit();
        doNothing().when(entityManager).clear();

        // execute
        val actual = jpaNestableTransaction.runTransacted(supplier);

        // verify
        assertThat(actual, is(RETURN_VALUE));
        verify(entityTransaction).begin();
        verify(entityTransaction).commit();
        verify(supplier).get();
        verify(entityManager, times(2)).getTransaction();
        verify(entityManager).clear();
        verifyNoMoreInteractions(entityManager, supplier, entityTransaction);
    }

    @Test
    void runTransactedReturningValueWithRollbackException() {
        // prepare
        @SuppressWarnings("unchecked") val supplier = (Supplier<String>) mock(Supplier.class);
        val entityTransaction = mock(EntityTransaction.class);
        var testRollbackException = new RollbackException();

        when(entityManager.getTransaction())
                .thenReturn(entityTransaction, entityTransaction);
        doNothing().when(entityTransaction).begin();
        when(supplier.get())
                .thenReturn(supplyValue());
        doThrow(testRollbackException).when(entityTransaction).commit();

        // execute
        Assertions.assertThrows(RollbackException.class, () ->
                jpaNestableTransaction.runTransacted(supplier)
        );

        // verify
        verify(entityTransaction).begin();
        verify(entityTransaction).commit();
        verify(supplier).get();
        verify(entityManager, times(2)).getTransaction();
        verifyNoMoreInteractions(entityManager, supplier, entityTransaction);
    }

    @Test
    void runTransactedReturningValueNested() {
        // prepare
        @SuppressWarnings("unchecked") val supplier = (Supplier<String>) mock(Supplier.class);
        val entityTransaction = mock(EntityTransaction.class);

        when(entityManager.getTransaction())
                .thenReturn(entityTransaction, entityTransaction);
        doNothing().when(entityTransaction).begin();
        when(supplier.get())
                .thenReturn(supplyValue());
        doNothing().when(entityTransaction).commit();
        doNothing().when(entityManager).clear();

        // execute
        val actual = jpaNestableTransaction.runTransacted(() -> jpaNestableTransaction.runTransacted(supplier));

        // verify
        assertThat(actual, is(RETURN_VALUE));
        verify(entityTransaction).begin();
        verify(entityTransaction).commit();
        verify(supplier).get();
        verify(entityManager, times(2)).getTransaction();
        verify(entityManager).clear();
        verifyNoMoreInteractions(entityManager, supplier, entityTransaction);
    }

    @Test
    void runTransactedReturningValueClearCacheTrue() {
        // prepare
        @SuppressWarnings("unchecked") val supplier = (Supplier<String>) mock(Supplier.class);
        val entityTransaction = mock(EntityTransaction.class);

        when(entityManager.getTransaction())
                .thenReturn(entityTransaction, entityTransaction);
        doNothing().when(entityTransaction).begin();
        when(supplier.get())
                .thenReturn(supplyValue());
        doNothing().when(entityTransaction).commit();
        doNothing().when(entityManager).clear();

        // execute
        val actual = jpaNestableTransaction.runTransacted(supplier, true);

        // verify
        assertThat(actual, is(RETURN_VALUE));
        verify(entityTransaction).begin();
        verify(entityTransaction).commit();
        verify(supplier).get();
        verify(entityManager, times(2)).getTransaction();
        verify(entityManager).clear();
        verifyNoMoreInteractions(entityManager, supplier, entityTransaction);
    }

    @Test
    void runTransactedReturningValueClearCacheFalse() {
        // prepare
        @SuppressWarnings("unchecked") val supplier = (Supplier<String>) mock(Supplier.class);
        val entityTransaction = mock(EntityTransaction.class);

        when(entityManager.getTransaction())
                .thenReturn(entityTransaction, entityTransaction);
        doNothing().when(entityTransaction).begin();
        when(supplier.get())
                .thenReturn(supplyValue());
        doNothing().when(entityTransaction).commit();

        // execute
        val actual = jpaNestableTransaction.runTransacted(supplier, false);

        // verify
        assertThat(actual, is(RETURN_VALUE));
        verify(entityTransaction).begin();
        verify(entityTransaction).commit();
        verify(supplier).get();
        verify(entityManager, times(2)).getTransaction();
        verifyNoMoreInteractions(entityManager, supplier, entityTransaction);
    }

    @Test
    void runTransactedReturningValueClearCacheTrueThenFalse() {
        // prepare
        @SuppressWarnings("unchecked") val supplier = (Supplier<String>) mock(Supplier.class);
        val entityTransaction = mock(EntityTransaction.class);

        when(entityManager.getTransaction())
                .thenReturn(entityTransaction, entityTransaction);
        doNothing().when(entityTransaction).begin();
        when(supplier.get())
                .thenReturn(supplyValue() + 1,supplyValue() + 2);
        doNothing().when(entityTransaction).commit();
        doNothing().when(entityManager).clear();

        // execute
        val actual1 = jpaNestableTransaction.runTransacted(supplier, true);
        val actual2 = jpaNestableTransaction.runTransacted(supplier, false);

        // verify
        assertThat(actual1, is(RETURN_VALUE + 1));
        assertThat(actual2, is(RETURN_VALUE + 2));
        verify(entityTransaction, times(2)).begin();
        verify(entityTransaction, times(2)).commit();
        verify(supplier, times(2)).get();
        verify(entityManager, times(4)).getTransaction();
        verify(entityManager).clear(); // <- only once!
        verifyNoMoreInteractions(entityManager, supplier, entityTransaction);
    }

    @Test
    void runTransactedReturningValueClearCacheTrueWithRollbackException() {
        // prepare
        @SuppressWarnings("unchecked") val supplier = (Supplier<String>) mock(Supplier.class);
        val entityTransaction = mock(EntityTransaction.class);
        var testRollbackException = new RollbackException();

        when(entityManager.getTransaction())
                .thenReturn(entityTransaction, entityTransaction);
        doNothing().when(entityTransaction).begin();
        when(supplier.get())
                .thenReturn(supplyValue());
        doThrow(testRollbackException).when(entityTransaction).commit();

        // execute
        Assertions.assertThrows(RollbackException.class, () ->
                jpaNestableTransaction.runTransacted(supplier, true)
        );

        // verify
        verify(entityTransaction).begin();
        verify(entityTransaction).commit();
        verify(supplier).get();
        verify(entityManager, times(2)).getTransaction();
        verifyNoMoreInteractions(entityManager, supplier, entityTransaction);
    }

    @Test
    void runTransactedReturningValueClearCacheFalseWithRollbackException() {
        // prepare
        @SuppressWarnings("unchecked") val supplier = (Supplier<String>) mock(Supplier.class);
        val entityTransaction = mock(EntityTransaction.class);
        var testRollbackException = new RollbackException();

        when(entityManager.getTransaction())
                .thenReturn(entityTransaction, entityTransaction);
        doNothing().when(entityTransaction).begin();
        when(supplier.get())
                .thenReturn(supplyValue());
        doThrow(testRollbackException).when(entityTransaction).commit();

        // execute
        Assertions.assertThrows(RollbackException.class, () ->
                jpaNestableTransaction.runTransacted(supplier, false)
        );

        // verify
        verify(entityTransaction).begin();
        verify(entityTransaction).commit();
        verify(supplier).get();
        verify(entityManager, times(2)).getTransaction();
        verifyNoMoreInteractions(entityManager, supplier, entityTransaction);
    }

    @Test
    void runTransactedReturningValueClearCacheTrueNested() {
        // prepare
        @SuppressWarnings("unchecked") val supplier = (Supplier<String>) mock(Supplier.class);
        val entityTransaction = mock(EntityTransaction.class);

        when(entityManager.getTransaction())
                .thenReturn(entityTransaction, entityTransaction);
        doNothing().when(entityTransaction).begin();
        when(supplier.get())
                .thenReturn(supplyValue());
        doNothing().when(entityTransaction).commit();
        doNothing().when(entityManager).clear();

        // execute
        val actual = jpaNestableTransaction.runTransacted(
                () -> jpaNestableTransaction.runTransacted(supplier, true),
                true
        );

        // verify
        assertThat(actual, is(RETURN_VALUE));
        verify(entityTransaction).begin();
        verify(entityTransaction).commit();
        verify(supplier).get();
        verify(entityManager, times(2)).getTransaction();
        verify(entityManager).clear();
        verifyNoMoreInteractions(entityManager, supplier, entityTransaction);
    }

    @Test
    void runTransactedReturningValueClearCacheFalseTrueNested() {
        // prepare
        @SuppressWarnings("unchecked") val supplier = (Supplier<String>) mock(Supplier.class);
        val entityTransaction = mock(EntityTransaction.class);

        when(entityManager.getTransaction())
                .thenReturn(entityTransaction, entityTransaction);
        doNothing().when(entityTransaction).begin();
        when(supplier.get())
                .thenReturn(supplyValue());
        doNothing().when(entityTransaction).commit();

        // execute
        val actual = jpaNestableTransaction.runTransacted(
                () -> jpaNestableTransaction.runTransacted(supplier, false),
                false
        );

        // verify
        assertThat(actual, is(RETURN_VALUE));
        verify(entityTransaction).begin();
        verify(entityTransaction).commit();
        verify(supplier).get();
        verify(entityManager, times(2)).getTransaction();
        verifyNoMoreInteractions(entityManager, supplier, entityTransaction);
    }

    @Test
    void runTransactedReturningValueClearCacheOuterTrueNested() {
        // prepare
        @SuppressWarnings("unchecked") val supplier = (Supplier<String>) mock(Supplier.class);
        val entityTransaction = mock(EntityTransaction.class);

        when(entityManager.getTransaction())
                .thenReturn(entityTransaction, entityTransaction);
        doNothing().when(entityTransaction).begin();
        when(supplier.get())
                .thenReturn(supplyValue());
        doNothing().when(entityTransaction).commit();
        doNothing().when(entityManager).clear();

        // execute
        val actual = jpaNestableTransaction.runTransacted(
                () -> jpaNestableTransaction.runTransacted(supplier, false),
                true
        );

        // verify
        assertThat(actual, is(RETURN_VALUE));
        verify(entityTransaction).begin();
        verify(entityTransaction).commit();
        verify(supplier).get();
        verify(entityManager, times(2)).getTransaction();
        verify(entityManager).clear();
        verifyNoMoreInteractions(entityManager, supplier, entityTransaction);
    }

    @Test
    void runTransactedReturningValueClearCacheInnerTrueTrueNested() {
        // prepare
        @SuppressWarnings("unchecked") val supplier = (Supplier<String>) mock(Supplier.class);
        val entityTransaction = mock(EntityTransaction.class);

        when(entityManager.getTransaction())
                .thenReturn(entityTransaction, entityTransaction);
        doNothing().when(entityTransaction).begin();
        when(supplier.get())
                .thenReturn(supplyValue());
        doNothing().when(entityTransaction).commit();
        doNothing().when(entityManager).clear();

        // execute
        val actual = jpaNestableTransaction.runTransacted(
                () -> jpaNestableTransaction.runTransacted(supplier, true),
                false
        );

        // verify
        assertThat(actual, is(RETURN_VALUE));
        verify(entityTransaction).begin();
        verify(entityTransaction).commit();
        verify(supplier).get();
        verify(entityManager, times(2)).getTransaction();
        verify(entityManager).clear();
        verifyNoMoreInteractions(entityManager, supplier, entityTransaction);
    }

    @Test
    void runTransactedReturningValueWithRollackHandler() {
        // prepare
        @SuppressWarnings("unchecked") val supplier = (Supplier<String>) mock(Supplier.class);
        @SuppressWarnings("unchecked") val exceptionHandler = (Function<RollbackException, String>) mock(Function.class);
        val entityTransaction = mock(EntityTransaction.class);

        when(entityManager.getTransaction())
                .thenReturn(entityTransaction, entityTransaction);
        doNothing().when(entityTransaction).begin();
        when(supplier.get())
                .thenReturn(supplyValue());
        doNothing().when(entityTransaction).commit();
        doNothing().when(entityManager).clear();

        // execute
        val actual = jpaNestableTransaction.runTransacted(supplier, exceptionHandler);

        // verify
        assertThat(actual, is(RETURN_VALUE));
        verify(entityTransaction).begin();
        verify(entityTransaction).commit();
        verify(supplier).get();
        verify(entityManager, times(2)).getTransaction();
        verify(entityManager).clear();
        verifyNoMoreInteractions(entityManager, supplier, entityTransaction);
        verifyNoInteractions(exceptionHandler);
    }

    @Test
    void runTransactedReturningValueWithRollackHandlerThrowingWithRollbackException() {
        // prepare
        @SuppressWarnings("unchecked") val supplier = (Supplier<String>) mock(Supplier.class);
        @SuppressWarnings("unchecked") val exceptionHandler = (Function<RollbackException, String>) mock(Function.class);
        val entityTransaction = mock(EntityTransaction.class);
        val testRollbackException = new RollbackException();

        when(entityManager.getTransaction())
                .thenReturn(entityTransaction, entityTransaction);
        doNothing().when(entityTransaction).begin();
        when(supplier.get())
                .thenReturn(supplyValue());
        doThrow(testRollbackException).when(entityTransaction).commit();
        doThrow(testRollbackException).when(exceptionHandler).apply(testRollbackException);

        // execute
        Assertions.assertThrows(RollbackException.class, () ->
                jpaNestableTransaction.runTransacted(supplier, exceptionHandler)
        );

        // verify
        verify(entityTransaction).begin();
        verify(entityTransaction).commit();
        verify(supplier).get();
        verify(entityManager, times(2)).getTransaction();
        verifyNoMoreInteractions(entityManager, supplier, entityTransaction, exceptionHandler);
        verifyNoMoreInteractions(exceptionHandler);
    }

    @Test
    void runTransactedReturningValueWithRollackHandlerDoNothingWithRollbackException() {
        // prepare
        val exceptionHandlerReturnValue = "ExceptionHandlerReturnValue";
        @SuppressWarnings("unchecked") val supplier = (Supplier<String>) mock(Supplier.class);
        @SuppressWarnings("unchecked") val exceptionHandler = (Function<RollbackException, String>) mock(Function.class);
        val entityTransaction = mock(EntityTransaction.class);
        val testRollbackException = new RollbackException();

        when(entityManager.getTransaction())
                .thenReturn(entityTransaction, entityTransaction);
        doNothing().when(entityTransaction).begin();
        when(supplier.get())
                .thenReturn(supplyValue());
        doThrow(testRollbackException).when(entityTransaction).commit();
        when(exceptionHandler.apply(testRollbackException))
                .thenReturn(exceptionHandlerReturnValue);

        // execute
        val actual = jpaNestableTransaction.runTransacted(supplier, exceptionHandler);

        // verify
        assertThat(actual, is(exceptionHandlerReturnValue));
        verify(entityTransaction).begin();
        verify(entityTransaction).commit();
        verify(supplier).get();
        verify(entityManager, times(2)).getTransaction();
        verifyNoMoreInteractions(entityManager, supplier, entityTransaction, exceptionHandler);
        verifyNoMoreInteractions(exceptionHandler);
    }

    @Test
    void runTransactedReturningValueWithRollackHandlerNested() {
        // prepare
        @SuppressWarnings("unchecked") val supplier = (Supplier<String>) mock(Supplier.class);
        @SuppressWarnings("unchecked") val exceptionHandler = (Function<RollbackException, String>) mock(Function.class);
        val entityTransaction = mock(EntityTransaction.class);

        when(entityManager.getTransaction())
                .thenReturn(entityTransaction, entityTransaction);
        doNothing().when(entityTransaction).begin();
        when(supplier.get())
                .thenReturn(supplyValue());
        doNothing().when(entityTransaction).commit();
        doNothing().when(entityManager).clear();

        // execute
        val actual = jpaNestableTransaction.runTransacted(
                () -> jpaNestableTransaction.runTransacted(supplier, exceptionHandler),
                exceptionHandler
        );

        // verify
        assertThat(actual, is(RETURN_VALUE));
        verify(entityTransaction).begin();
        verify(entityTransaction).commit();
        verify(supplier).get();
        verify(entityManager, times(2)).getTransaction();
        verify(entityManager).clear();
        verifyNoMoreInteractions(entityManager, supplier, entityTransaction);
        verifyNoInteractions(exceptionHandler);
    }

    @Test
    void runTransactedReturningValueWithRollackHandlerThrowingSameExceptionNestedWithRollbackException() {
        // prepare
        @SuppressWarnings("unchecked") val supplier = (Supplier<String>) mock(Supplier.class);
        @SuppressWarnings("unchecked") val exceptionHandlerInner = (Function<RollbackException, String>) mock(Function.class);
        @SuppressWarnings("unchecked") val exceptionHandlerOuter = (Function<RollbackException, String>) mock(Function.class);
        val entityTransaction = mock(EntityTransaction.class);
        val testRollbackException = new RollbackException("RBE");

        when(entityManager.getTransaction())
                .thenReturn(entityTransaction, entityTransaction);
        doNothing().when(entityTransaction).begin();
        when(supplier.get())
                .thenReturn(supplyValue());
        doThrow(testRollbackException).when(entityTransaction).commit();
        doThrow(testRollbackException).when(exceptionHandlerInner).apply(testRollbackException);
        doThrow(testRollbackException).when(exceptionHandlerOuter).apply(testRollbackException);

        // execute
        val actualException = Assertions.assertThrows(RollbackException.class, () ->
                jpaNestableTransaction.runTransacted(
                        () -> jpaNestableTransaction.runTransacted(supplier, exceptionHandlerInner),
                        exceptionHandlerOuter
                )
        );

        // verify
        assertThat(actualException, is(testRollbackException));
        assertThat(actualException.getSuppressed(), emptyArray());
        verify(entityTransaction).begin();
        verify(entityTransaction).commit();
        verify(supplier).get();
        verify(entityManager, times(2)).getTransaction();
        verifyNoMoreInteractions(entityManager, supplier, entityTransaction);
        verifyNoMoreInteractions(exceptionHandlerInner, exceptionHandlerOuter);
    }

    @Test
    void runTransactedReturningValueWithRollackHandlerInnerThrowingDifferentExceptionNestedWithRollbackException() {
        // prepare
        @SuppressWarnings("unchecked") val supplier = (Supplier<String>) mock(Supplier.class);
        @SuppressWarnings("unchecked") val exceptionHandlerInner = (Function<RollbackException, String>) mock(Function.class);
        @SuppressWarnings("unchecked") val exceptionHandlerOuter = (Function<RollbackException, String>) mock(Function.class);
        val entityTransaction = mock(EntityTransaction.class);
        val testRollbackException1 = new RollbackException("RBE1");
        val testRollbackException2 = new RollbackException("RBE2");

        when(entityManager.getTransaction())
                .thenReturn(entityTransaction, entityTransaction);
        doNothing().when(entityTransaction).begin();
        when(supplier.get())
                .thenReturn(supplyValue());
        doThrow(testRollbackException1).when(entityTransaction).commit();
        doThrow(testRollbackException2).when(exceptionHandlerInner).apply(testRollbackException1);
        doThrow(testRollbackException2).when(exceptionHandlerOuter).apply(testRollbackException2);

        // execute
        val actualException = Assertions.assertThrows(RollbackException.class, () ->
                jpaNestableTransaction.runTransacted(
                        () -> jpaNestableTransaction.runTransacted(supplier, exceptionHandlerInner),
                        exceptionHandlerOuter
                )
        );

        // verify
        assertThat(actualException, is(testRollbackException2));
        assertThat(actualException.getSuppressed(), arrayContaining(testRollbackException1));
        verify(entityTransaction).begin();
        verify(entityTransaction).commit();
        verify(supplier).get();
        verify(entityManager, times(2)).getTransaction();
        verifyNoMoreInteractions(entityManager, supplier, entityTransaction);
        verifyNoMoreInteractions(exceptionHandlerInner, exceptionHandlerOuter);
    }

    @Test
    void runTransactedReturningValueWithRollackHandlerOuterThrowingDifferentExceptionNestedWithRollbackException() {
        // prepare
        @SuppressWarnings("unchecked") val supplier = (Supplier<String>) mock(Supplier.class);
        @SuppressWarnings("unchecked") val exceptionHandlerInner = (Function<RollbackException, String>) mock(Function.class);
        @SuppressWarnings("unchecked") val exceptionHandlerOuter = (Function<RollbackException, String>) mock(Function.class);
        val entityTransaction = mock(EntityTransaction.class);
        val testRollbackException1 = new RollbackException("RBE1");
        val testRollbackException2 = new RollbackException("RBE2");

        when(entityManager.getTransaction())
                .thenReturn(entityTransaction, entityTransaction);
        doNothing().when(entityTransaction).begin();
        when(supplier.get())
                .thenReturn(supplyValue());
        doThrow(testRollbackException1).when(entityTransaction).commit();
        doThrow(testRollbackException1).when(exceptionHandlerInner).apply(testRollbackException1);
        doThrow(testRollbackException2).when(exceptionHandlerOuter).apply(testRollbackException1);

        // execute
        val actualException = Assertions.assertThrows(RollbackException.class, () ->
                jpaNestableTransaction.runTransacted(
                        () -> jpaNestableTransaction.runTransacted(supplier, exceptionHandlerInner),
                        exceptionHandlerOuter
                )
        );

        // verify
        assertThat(actualException, is(testRollbackException2));
        assertThat(actualException.getSuppressed(), arrayContaining(testRollbackException1));
        verify(entityTransaction).begin();
        verify(entityTransaction).commit();
        verify(supplier).get();
        verify(entityManager, times(2)).getTransaction();
        verifyNoMoreInteractions(entityManager, supplier, entityTransaction);
        verifyNoMoreInteractions(exceptionHandlerInner, exceptionHandlerOuter);
    }

    @Test
    void runTransactedReturningValueWithRollackHandlerDoNothingNestedWithRollbackException() {
        // prepare
        val exceptionHandlerReturnValue1 = "ExceptionHandlerReturnValue1";
        val exceptionHandlerReturnValue2 = "ExceptionHandlerReturnValue2";
        @SuppressWarnings("unchecked") val supplier = (Supplier<String>) mock(Supplier.class);
        @SuppressWarnings("unchecked") val exceptionHandlerInner = (Function<RollbackException, String>) mock(Function.class);
        @SuppressWarnings("unchecked") val exceptionHandlerOuter = (Function<RollbackException, String>) mock(Function.class);
        val entityTransaction = mock(EntityTransaction.class);
        val testRollbackException = new RollbackException();

        when(entityManager.getTransaction())
                .thenReturn(entityTransaction, entityTransaction);
        doNothing().when(entityTransaction).begin();
        when(supplier.get())
                .thenReturn(supplyValue());
        doThrow(testRollbackException).when(entityTransaction).commit();
        when(exceptionHandlerInner.apply(testRollbackException))
                .thenReturn(exceptionHandlerReturnValue1);
        when(exceptionHandlerOuter.apply(testRollbackException))
                .thenReturn(exceptionHandlerReturnValue2);

        // execute
        val actual = jpaNestableTransaction.runTransacted(
                () -> jpaNestableTransaction.runTransacted(supplier, exceptionHandlerInner),
                exceptionHandlerOuter
        );

        // verify
        assertThat(actual, is(exceptionHandlerReturnValue2));
        assertThat(testRollbackException.getSuppressed(), emptyArray());
        verify(entityTransaction).begin();
        verify(entityTransaction).commit();
        verify(supplier).get();
        verify(entityManager, times(2)).getTransaction();
        verifyNoMoreInteractions(entityManager, supplier, entityTransaction);
        verifyNoMoreInteractions(exceptionHandlerInner, exceptionHandlerOuter);
    }

    @Test
    void runTransactedReturningValueWithRollackHandlerInnerDoNothingOuterThrowingSameExceptionNestedWithRollbackException() {
        // prepare
        val exceptionHandlerReturnValue = "ExceptionHandlerReturnValue";
        @SuppressWarnings("unchecked") val supplier = (Supplier<String>) mock(Supplier.class);
        @SuppressWarnings("unchecked") val exceptionHandlerInner = (Function<RollbackException, String>) mock(Function.class);
        @SuppressWarnings("unchecked") val exceptionHandlerOuter = (Function<RollbackException, String>) mock(Function.class);
        val entityTransaction = mock(EntityTransaction.class);
        val testRollbackException = new RollbackException("RBE1");

        when(entityManager.getTransaction())
                .thenReturn(entityTransaction, entityTransaction);
        doNothing().when(entityTransaction).begin();
        when(supplier.get())
                .thenReturn(supplyValue());
        doThrow(testRollbackException).when(entityTransaction).commit();
        when(exceptionHandlerInner.apply(testRollbackException))
                .thenReturn(exceptionHandlerReturnValue);
        doThrow(testRollbackException).when(exceptionHandlerOuter).apply(testRollbackException);

        // execute
        val actualException = Assertions.assertThrows(RollbackException.class, () ->
                jpaNestableTransaction.runTransacted(
                        () -> jpaNestableTransaction.runTransacted(supplier, exceptionHandlerInner),
                        exceptionHandlerOuter
                )
        );

        // verify
        assertThat(actualException, is(testRollbackException));
        assertThat(actualException.getSuppressed(), emptyArray());
        verify(entityTransaction).begin();
        verify(entityTransaction).commit();
        verify(supplier).get();
        verify(entityManager, times(2)).getTransaction();
        verifyNoMoreInteractions(entityManager, supplier, entityTransaction);
        verifyNoMoreInteractions(exceptionHandlerInner, exceptionHandlerOuter);
    }

    @Test
    void runTransactedReturningValueWithRollackHandlerInnerDoNothingOuterThrowingDifferentExceptionNestedWithRollbackException() {
        // prepare
        val exceptionHandlerReturnValue = "ExceptionHandlerReturnValue";
        @SuppressWarnings("unchecked") val supplier = (Supplier<String>) mock(Supplier.class);
        @SuppressWarnings("unchecked") val exceptionHandlerInner = (Function<RollbackException, String>) mock(Function.class);
        @SuppressWarnings("unchecked") val exceptionHandlerOuter = (Function<RollbackException, String>) mock(Function.class);
        val entityTransaction = mock(EntityTransaction.class);
        val testRollbackException1 = new RollbackException("RBE1");
        val testRollbackException2 = new RollbackException("RBE2");

        when(entityManager.getTransaction())
                .thenReturn(entityTransaction, entityTransaction);
        doNothing().when(entityTransaction).begin();
        when(supplier.get())
                .thenReturn(supplyValue());
        doThrow(testRollbackException1).when(entityTransaction).commit();
        when(exceptionHandlerInner.apply(testRollbackException1))
                .thenReturn(exceptionHandlerReturnValue);
        doThrow(testRollbackException2).when(exceptionHandlerOuter).apply(testRollbackException1);

        // execute
        val actualException = Assertions.assertThrows(RollbackException.class, () ->
                jpaNestableTransaction.runTransacted(
                        () -> jpaNestableTransaction.runTransacted(supplier, exceptionHandlerInner),
                        exceptionHandlerOuter
                )
        );

        // verify
        assertThat(actualException, is(testRollbackException2));
        assertThat(actualException.getSuppressed(), arrayContaining(testRollbackException1));
        verify(entityTransaction).begin();
        verify(entityTransaction).commit();
        verify(supplier).get();
        verify(entityManager, times(2)).getTransaction();
        verifyNoMoreInteractions(entityManager, supplier, entityTransaction);
        verifyNoMoreInteractions(exceptionHandlerInner, exceptionHandlerOuter);
    }

    @Test
    void runTransactedReturningValueWithRollackHandlerInnerThrowingSameExceptionOuterDoNothingNestedWithRollbackException() {
        // prepare
        val exceptionHandlerReturnValue = "ExceptionHandlerReturnValue";
        @SuppressWarnings("unchecked") val supplier = (Supplier<String>) mock(Supplier.class);
        @SuppressWarnings("unchecked") val exceptionHandlerInner = (Function<RollbackException, String>) mock(Function.class);
        @SuppressWarnings("unchecked") val exceptionHandlerOuter = (Function<RollbackException, String>) mock(Function.class);
        val entityTransaction = mock(EntityTransaction.class);
        val testRollbackException = new RollbackException("RBE1");

        when(entityManager.getTransaction())
                .thenReturn(entityTransaction, entityTransaction);
        doNothing().when(entityTransaction).begin();
        when(supplier.get())
                .thenReturn(supplyValue());
        doThrow(testRollbackException).when(entityTransaction).commit();
        doThrow(testRollbackException).when(exceptionHandlerInner).apply(testRollbackException);
        when(exceptionHandlerOuter.apply(testRollbackException))
                .thenReturn(exceptionHandlerReturnValue);

        // execute
        val actual = jpaNestableTransaction.runTransacted(
                () -> jpaNestableTransaction.runTransacted(supplier, exceptionHandlerInner),
                exceptionHandlerOuter
        );

        // verify
        assertThat(actual, is(exceptionHandlerReturnValue));
        assertThat(testRollbackException.getSuppressed(), emptyArray());
        verify(entityTransaction).begin();
        verify(entityTransaction).commit();
        verify(supplier).get();
        verify(entityManager, times(2)).getTransaction();
        verifyNoMoreInteractions(entityManager, supplier, entityTransaction);
        verifyNoMoreInteractions(exceptionHandlerInner, exceptionHandlerOuter);
    }

    @Test
    void runTransactedReturningValueWithRollackHandlerInnerThrowingDifferentExceptionOuterDoNothingNestedWithRollbackException() {
        // prepare
        val exceptionHandlerReturnValue = "ExceptionHandlerReturnValue";
        @SuppressWarnings("unchecked") val supplier = (Supplier<String>) mock(Supplier.class);
        @SuppressWarnings("unchecked") val exceptionHandlerInner = (Function<RollbackException, String>) mock(Function.class);
        @SuppressWarnings("unchecked") val exceptionHandlerOuter = (Function<RollbackException, String>) mock(Function.class);
        val entityTransaction = mock(EntityTransaction.class);
        val testRollbackException1 = new RollbackException("RBE1");
        val testRollbackException2 = new RollbackException("RBE2");

        when(entityManager.getTransaction())
                .thenReturn(entityTransaction, entityTransaction);
        doNothing().when(entityTransaction).begin();
        when(supplier.get())
                .thenReturn(supplyValue());
        doThrow(testRollbackException1).when(entityTransaction).commit();
        doThrow(testRollbackException2).when(exceptionHandlerInner).apply(testRollbackException1);
        when(exceptionHandlerOuter.apply(testRollbackException2))
                .thenReturn(exceptionHandlerReturnValue);

        // execute
        val actual = jpaNestableTransaction.runTransacted(
                () -> jpaNestableTransaction.runTransacted(supplier, exceptionHandlerInner),
                exceptionHandlerOuter
        );

        // verify
        assertThat(actual, is(exceptionHandlerReturnValue));
        assertThat(testRollbackException2.getSuppressed(), arrayContaining(testRollbackException1));
        verify(entityTransaction).begin();
        verify(entityTransaction).commit();
        verify(supplier).get();
        verify(entityManager, times(2)).getTransaction();
        verifyNoMoreInteractions(entityManager, supplier, entityTransaction);
        verifyNoMoreInteractions(exceptionHandlerInner, exceptionHandlerOuter);
    }
}