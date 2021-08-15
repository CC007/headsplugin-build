package com.github.cc007.headsplugin.dagger.modules;

import com.github.cc007.headsplugin.business.services.NbtService;

import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public abstract class ServiceModule {

    @Provides
    @Singleton
    static NbtService provideNbtService() {
        return new NbtService();
    }
}
