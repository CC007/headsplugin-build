package com.github.cc007.headsplugin.dagger.modules;

import com.github.cc007.headsplugin.api.business.services.Profiler;
import com.github.cc007.headsplugin.business.services.NbtService;
import com.github.cc007.headsplugin.business.services.ProfilerImpl;
import com.github.cc007.headsplugin.config.properties.ConfigProperties;

import dagger.Module;
import dagger.Provides;
import org.apache.logging.log4j.Level;

import javax.inject.Singleton;

@Module
public abstract class ServiceModule {

    @Provides
    @Singleton
    static NbtService provideNbtService() {
        return new NbtService();
    }

    @Provides
    @Singleton
    static Profiler provideProfiler(ConfigProperties configProperties) {
        return new ProfilerImpl(Level.toLevel(configProperties.getProfiler().getDefaultLogLevel(), Level.DEBUG));
    }
}
