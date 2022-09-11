package com.github.cc007.headsplugin.business.services;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.atomic.AtomicReference;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfilerImplTest {

    ProfilerImpl profiler;

    @Mock(lenient = true)
    private Appender appenderMock;

    private Appender realAppender;

    @Captor
    private ArgumentCaptor<LogEvent> captorLoggingEvent;

    private Logger logger;

    @BeforeEach
    void setUp() {
        when(appenderMock.getName()).thenReturn("MockAppender");
        when(appenderMock.isStarted()).thenReturn(true);

        logger = (Logger) LogManager.getLogger(ProfilerImpl.class);
        logger.addAppender(appenderMock);
        logger.setLevel(Level.DEBUG);

        realAppender = logger.getParent().getAppenders().get("DefaultConsole-2");
        logger.getParent().removeAppender(realAppender);


        profiler = new ProfilerImpl(Level.DEBUG);
    }

    @AfterEach
    void tearDown() {
        logger.removeAppender(appenderMock);
        logger.getParent().addAppender(realAppender);
    }

    @Test
    void runProfiledWithSupplier() {
        // prepare
        final var expected = "result";

        // execute
        final var actual = profiler.runProfiled(() -> expected);

        // verify
        assertThat(actual, is(expected));

        verify(appenderMock).append(captorLoggingEvent.capture());
        final var logEvents = captorLoggingEvent.getAllValues();
        assertThat(logEvents.size(), is(1));
        final var logEvent = logEvents.get(0);
        assertThat(logEvent.getMessage().getFormattedMessage(), startsWith("Done in "));
    }

    @Test
    void runProfiledWithSupplierAndDoneMessage() {
        // prepare
        final var expected = "result";
        final var testDoneMessage = "DoneMessage";

        // execute
        final var actual = profiler.runProfiled(testDoneMessage, () -> expected);

        // verify
        assertThat(actual, is(expected));

        verify(appenderMock).append(captorLoggingEvent.capture());
        final var logEvents = captorLoggingEvent.getAllValues();
        assertThat(logEvents.size(), is(1));
        final var logEvent = logEvents.get(0);
        assertThat(logEvent.getMessage().getFormattedMessage(), startsWith(testDoneMessage + " in "));
    }

    @Test
    void runProfiledWithSupplierAndLogLevel() {
        // prepare
        final var expected = "result";
        final var testLogLevel = Level.ERROR;

        // execute
        final var actual = profiler.runProfiled(testLogLevel, () -> expected);

        // verify
        assertThat(actual, is(expected));

        verify(appenderMock).append(captorLoggingEvent.capture());
        final var logEvents = captorLoggingEvent.getAllValues();
        assertThat(logEvents.size(), is(1));
        final var logEvent = logEvents.get(0);
        assertThat(logEvent.getMessage().getFormattedMessage(), startsWith("Done in "));
    }

    @Test
    void runProfiledWithSupplierAndLogLevelWithMinLevelWarn() {
        // prepare
        final var expected = "result";
        final var testLogLevel = Level.INFO;
        final var testMinLogLevel = Level.WARN;

        logger.setLevel(testMinLogLevel);

        // execute
        final var actual = profiler.runProfiled(testLogLevel, () -> expected);

        // verify
        assertThat(actual, is(expected));

        verify(appenderMock, times(0)).append(any());
    }

    @Test
    void runProfiledWithSupplierAndLogLevelAndDoneMessage() {
        // prepare
        final var expected = "result";
        final var testDoneMessage = "DoneMessage";
        final var testLogLevel = Level.ERROR;

        // execute
        final var actual = profiler.runProfiled(testLogLevel, testDoneMessage, () -> expected);

        // verify
        assertThat(actual, is(expected));

        verify(appenderMock).append(captorLoggingEvent.capture());
        final var logEvents = captorLoggingEvent.getAllValues();
        assertThat(logEvents.size(), is(1));
        final var logEvent = logEvents.get(0);
        assertThat(logEvent.getMessage().getFormattedMessage(), startsWith(testDoneMessage + " in "));
    }

    @Test
    void runProfiledWithSupplierAndLogLevelAndDoneMessageWithMinLevelWarn() {
        // prepare
        final var expected = "result";
        final var testDoneMessage = "DoneMessage";
        final var testLogLevel = Level.INFO;
        final var testMinLogLevel = Level.WARN;

        logger.setLevel(testMinLogLevel);

        // execute
        final var actual = profiler.runProfiled(testLogLevel, testDoneMessage, () -> expected);

        // verify
        assertThat(actual, is(expected));

        verify(appenderMock, times(0)).append(any());
    }

    @Test
    void runProfiledWithRunnable() {
        // prepare
        final var expected = "result";

        // execute
        final var actual = new AtomicReference<String>();
        //noinspection CodeBlock2Expr
        profiler.runProfiled(() -> {
            actual.set(expected);
        });

        // verify
        assertThat(actual.get(), is(expected));

        verify(appenderMock).append(captorLoggingEvent.capture());
        final var logEvents = captorLoggingEvent.getAllValues();
        assertThat(logEvents.size(), is(1));
        final var logEvent = logEvents.get(0);
        assertThat(logEvent.getMessage().getFormattedMessage(), startsWith("Done in "));
    }

    @Test
    void runProfiledWithRunnableAndDoneMessage() {
        // prepare
        final var expected = "result";
        final var testDoneMessage = "DoneMessage";

        // execute
        final var actual = new AtomicReference<String>();
        //noinspection CodeBlock2Expr
        profiler.runProfiled(testDoneMessage, () -> {
            actual.set(expected);
        });

        // verify
        assertThat(actual.get(), is(expected));

        verify(appenderMock).append(captorLoggingEvent.capture());
        final var logEvents = captorLoggingEvent.getAllValues();
        assertThat(logEvents.size(), is(1));
        final var logEvent = logEvents.get(0);
        assertThat(logEvent.getMessage().getFormattedMessage(), startsWith(testDoneMessage + " in "));
    }

    @Test
    void runProfiledWithRunnableAndLogLevel() {
        // prepare
        final var expected = "result";
        final var testLogLevel = Level.ERROR;

        // execute
        final var actual = new AtomicReference<String>();
        //noinspection CodeBlock2Expr
        profiler.runProfiled(testLogLevel, () -> {
            actual.set(expected);
        });

        // verify
        assertThat(actual.get(), is(expected));

        verify(appenderMock).append(captorLoggingEvent.capture());
        final var logEvents = captorLoggingEvent.getAllValues();
        assertThat(logEvents.size(), is(1));
        final var logEvent = logEvents.get(0);
        assertThat(logEvent.getMessage().getFormattedMessage(), startsWith("Done in "));
    }

    @Test
    void runProfiledWithRunnableAndLogLevelWithMinLevelWarn() {
        // prepare
        final var expected = "result";
        final var testLogLevel = Level.INFO;
        final var testMinLogLevel = Level.WARN;

        logger.setLevel(testMinLogLevel);

        // execute
        final var actual = new AtomicReference<String>();
        //noinspection CodeBlock2Expr
        profiler.runProfiled(testLogLevel, () -> {
            actual.set(expected);
        });

        // verify
        assertThat(actual.get(), is(expected));

        verify(appenderMock, times(0)).append(any());
    }

    @Test
    void runProfiledWithRunnableAndLogLevelAndDoneMessage() {
        // prepare
        final var expected = "result";
        final var testDoneMessage = "DoneMessage";
        final var testLogLevel = Level.ERROR;

        // execute
        final var actual = new AtomicReference<String>();
        //noinspection CodeBlock2Expr
        profiler.runProfiled(testLogLevel, testDoneMessage, () -> {
            actual.set(expected);
        });

        // verify
        assertThat(actual.get(), is(expected));

        verify(appenderMock).append(captorLoggingEvent.capture());
        final var logEvents = captorLoggingEvent.getAllValues();
        assertThat(logEvents.size(), is(1));
        final var logEvent = logEvents.get(0);
        assertThat(logEvent.getMessage().getFormattedMessage(), startsWith(testDoneMessage + " in "));
    }

    @Test
    void runProfiledWithRunnableAndLogLevelAndDoneMessageWithMinLevelWarn() {
        // prepare
        final var expected = "result";
        final var testDoneMessage = "DoneMessage";
        final var testLogLevel = Level.INFO;
        final var testMinLogLevel = Level.WARN;

        logger.setLevel(testMinLogLevel);

        // execute
        final var actual = new AtomicReference<String>();
        //noinspection CodeBlock2Expr
        profiler.runProfiled(testLogLevel, testDoneMessage, () -> {
            actual.set(expected);
        });

        // verify
        assertThat(actual.get(), is(expected));

        verify(appenderMock, times(0)).append(any());
    }

}