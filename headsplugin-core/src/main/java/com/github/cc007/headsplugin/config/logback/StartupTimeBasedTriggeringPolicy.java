package com.github.cc007.headsplugin.config.logback;

import ch.qos.logback.core.joran.spi.NoAutoStart;
import ch.qos.logback.core.rolling.RolloverFailure;
import ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP;
import lombok.extern.slf4j.Slf4j;

@NoAutoStart
@Slf4j
public class StartupTimeBasedTriggeringPolicy<E> extends SizeAndTimeBasedFNATP<E> {
    @Override
    public void start() {
        super.start();
        nextCheck = 0;
        isTriggeringEvent(null, null);
        try {
            tbrp.rollover();
        } catch (RolloverFailure e) {
            log.warn("Rollover failed.");
        }

    }
}
