package com.github.cc007.headsplugin.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.boot.context.logging.LoggingApplicationListener;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.GenericApplicationListener;
import org.springframework.core.ResolvableType;

public class LoggingFixApplicationListener implements GenericApplicationListener {

    @Override
    public boolean supportsEventType(ResolvableType resolvableType) {
        return true;
    }

    @Override
    public boolean supportsSourceType(Class<?> sourceType) {
        return true;
    }

    @Override
    public int getOrder() {
        return LoggingApplicationListener.DEFAULT_ORDER + 1;
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ApplicationEnvironmentPreparedEvent) {
            final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
            final org.apache.logging.log4j.core.config.Configuration config = ctx.getConfiguration();
            config.getRootLogger().removeAppender("SysOut");
            ctx.updateLoggers();
        }
    }
}