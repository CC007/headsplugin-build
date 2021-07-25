package com.github.cc007.headsplugin.dagger;

import com.github.cc007.headsplugin.config.properties.ConfigProperties;
import com.github.cc007.headsplugin.dagger.modules.FeignModule;
import com.github.cc007.headsplugin.dagger.modules.HeadsPluginModule;
import com.github.cc007.headsplugin.dagger.modules.JpaModule;
import com.github.cc007.headsplugin.dagger.modules.MapperModule;

import dagger.Component;

@Component(modules = {HeadsPluginModule.class, FeignModule.class, JpaModule.class, MapperModule.class})
public interface HeadsPluginComponent {
    ConfigProperties configProperties();
    //StartupCategoryUpdater startupCategoryUpdater();
}
