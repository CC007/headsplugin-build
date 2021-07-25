package com.github.cc007.headsplugin.dagger.modules;

import com.github.cc007.headsplugin.business.services.HelloService;
import com.github.cc007.headsplugin.business.services.impl.HelloServiceImpl;

import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public abstract class ServiceModule {

    @Provides
    @Singleton
    static HelloService provideHelloServiceImpl() {
        return new HelloServiceImpl();
    }


}
