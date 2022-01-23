package com.github.cc007.headsplugin.dagger.modules;

import com.github.cc007.headsplugin.HeadsPlugin;
import com.github.cc007.headsplugin.config.properties.CategoriesProperties;
import com.github.cc007.headsplugin.config.properties.ConfigProperties;
import com.github.cc007.headsplugin.config.properties.HeadspluginProperties;

import dagger.Module;
import dagger.Provides;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;

import javax.inject.Singleton;
import java.util.Optional;

@Module
public abstract class ConfigModule {

    @Provides
    @Singleton
    static HeadsPlugin provideHeadsPlugin() {
        Optional<HeadsPlugin> headsPluginOptional = HeadsPlugin.getPlugin();
        if (headsPluginOptional.isEmpty()) {
            throw new IllegalStateException("HeadsPluginAPI has not been enabled yet");
        }
        return headsPluginOptional.get();
    }

    @Provides
    @Singleton
    static ConfigProperties provideConfigProperties(HeadsPlugin headsPlugin) {
        CustomClassLoaderConstructor configPropertiesClassLoader = new CustomClassLoaderConstructor(ConfigProperties.class.getClassLoader());
        configPropertiesClassLoader.getPropertyUtils().setSkipMissingProperties(true);
        Yaml yaml = new Yaml(configPropertiesClassLoader);
        return yaml.loadAs(headsPlugin.getResource("config.yml"), ConfigProperties.class);
    }

    @Provides
    @Singleton
    static HeadspluginProperties provideHeadspluginProperties(ConfigProperties configProperties) {
        return configProperties.getHeadsplugin();
    }

    @Provides
    @Singleton
    static CategoriesProperties provideCategoriesProperties(HeadspluginProperties headspluginProperties) {
        return headspluginProperties.getCategories();
    }
}
