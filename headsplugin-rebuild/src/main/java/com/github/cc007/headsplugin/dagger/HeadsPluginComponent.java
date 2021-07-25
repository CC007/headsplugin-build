package com.github.cc007.headsplugin.dagger;

import com.github.cc007.headsplugin.api.HeadsPluginServices;
import com.github.cc007.headsplugin.dagger.modules.FeignModule;
import com.github.cc007.headsplugin.dagger.modules.HeadsPluginModule;
import com.github.cc007.headsplugin.dagger.modules.JpaModule;
import com.github.cc007.headsplugin.dagger.modules.MapperModule;
import com.github.cc007.headsplugin.dagger.modules.ServiceModule;

import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = {
        HeadsPluginModule.class,
        FeignModule.class,
        JpaModule.class,
        ServiceModule.class,
        MapperModule.class
})
public interface HeadsPluginComponent extends HeadsPluginServices {
}
