package com.github.cc007.headsplugin.business.services;

import com.github.cc007.headsplugin.api.business.services.Profiler;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;

import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

@Log4j2
@RequiredArgsConstructor
public class ProfilerImpl implements Profiler {

    private static final String DEFAULT_DONE_MESSAGE = "Done";

    @NonNull
    private final Level defaultLogLevel;

    @Override
    public <T> T runProfiled(Supplier<T> supplier) {
        return runProfiled(DEFAULT_DONE_MESSAGE, supplier);
    }

    @Override
    public <T> T runProfiled(String doneMessage, Supplier<T> supplier) {
        return runProfiled(defaultLogLevel, doneMessage, supplier);
    }

    @Override
    public <T> T runProfiled(Level logLevel, Supplier<T> supplier) {
        return runProfiled(logLevel, DEFAULT_DONE_MESSAGE, supplier);
    }

    @Override
    public <T> T runProfiled(Level logLevel, String doneMessage, Supplier<T> supplier) {
        AtomicReference<T> value = new AtomicReference<>();
        runProfiled(logLevel, doneMessage, () -> value.set(supplier.get()));
        return value.get();
    }

    @Override
    public double runProfiled(Runnable runnable) {
        return runProfiled(DEFAULT_DONE_MESSAGE, runnable);
    }

    @Override
    public double runProfiled(String doneMessage, Runnable runnable) {
        return runProfiled(defaultLogLevel, doneMessage, runnable);
    }

    @Override
    public double runProfiled(Level logLevel, Runnable runnable) {
        return runProfiled(logLevel, DEFAULT_DONE_MESSAGE, runnable);
    }

    @Override
    public double runProfiled(Level logLevel, String doneMessage, Runnable runnable) {
        long start = System.currentTimeMillis();
        runnable.run();
        long end = System.currentTimeMillis();
        double duration = (end - start) / 1000.0;

        logDuration(logLevel, doneMessage, duration);
        return duration;
    }

    private void logDuration(Level logLevel, String message, double duration) {
        log.log(logLevel,
                "{} in {}s.",
                message,
                new DecimalFormat("0.000").format(duration));
    }
}
