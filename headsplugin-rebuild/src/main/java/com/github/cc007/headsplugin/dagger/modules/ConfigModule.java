package com.github.cc007.headsplugin.dagger.modules;

import com.github.cc007.headsplugin.api.HeadsPluginApi;
import com.github.cc007.headsplugin.config.properties.CategoriesProperties;
import com.github.cc007.headsplugin.config.properties.ConfigProperties;
import com.github.cc007.headsplugin.config.properties.HeadspluginProperties;

import dagger.Module;
import dagger.Provides;
import lombok.SneakyThrows;
import org.bukkit.plugin.Plugin;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;
import org.yaml.snakeyaml.representer.Representer;

import javax.inject.Singleton;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Optional;

@Module
public abstract class ConfigModule {

    @Provides
    @Singleton
    static Plugin providePlugin() {
        Optional<Plugin> headsPluginOptional = HeadsPluginApi.getPlugin();
        if (headsPluginOptional.isEmpty()) {
            throw new IllegalStateException("HeadsPluginAPI has not been enabled yet");
        }
        return headsPluginOptional.get();
    }

    @Provides
    @Singleton
    static ConfigProperties provideConfigProperties(Plugin plugin) {
        CustomClassLoaderConstructor configPropertiesClassLoader = new CustomClassLoaderConstructor(ConfigProperties.class.getClassLoader());
        configPropertiesClassLoader.getPropertyUtils().setSkipMissingProperties(true);
        Representer representer = new Representer();
        representer.getPropertyUtils().setSkipMissingProperties(true);
        Yaml yaml = new Yaml(configPropertiesClassLoader, representer);
        return yaml.loadAs(getConfigInputStream(plugin), ConfigProperties.class);
    }

    @SneakyThrows(FileNotFoundException.class)
    private static FileInputStream getConfigInputStream(Plugin plugin) {
        return new FileInputStream(new File(plugin.getDataFolder(), "config.yml"));
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
