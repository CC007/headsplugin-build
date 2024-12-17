package com.github.cc007.headsplugin.dagger.modules;

import com.github.cc007.headsplugin.api.HeadsPluginApi;
import com.github.cc007.headsplugin.business.model.McVersion;
import com.github.cc007.headsplugin.config.properties.CategoriesProperties;
import com.github.cc007.headsplugin.config.properties.ConfigProperties;
import com.github.cc007.headsplugin.config.properties.HeadspluginProperties;
import dagger.Module;
import dagger.Provides;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;
import org.yaml.snakeyaml.representer.Representer;

import javax.inject.Singleton;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Objects;
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
    static McVersion provideMcVersion() {
        final var mcVersionOptional = Optional.of(Bukkit.getBukkitVersion())
                .map(bukkitVersion -> bukkitVersion.split("-"))
                .filter(parts -> parts.length >= 1)
                .map(parts -> parts[0])
                .map(mcVersion -> mcVersion.split("\\."))
                .filter(parts -> parts.length >= 2)
                .map(parts -> Arrays.stream(parts)
                        .filter(part -> part.matches("\\d+"))
                        .map(Integer::parseInt)
                        .toArray(Integer[]::new)
                )
                .map(parts -> switch (parts.length) {
                    case 2 -> new McVersion(parts[0], parts[1], 0);
                    default -> new McVersion(parts[0], parts[1], parts[2]);
                });

        if (mcVersionOptional.isEmpty()) {
            throw new IllegalStateException("HeadsPluginAPI can't determine the minecraft version");
        }
        return mcVersionOptional.get();
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

    @Provides
    @Singleton
    static ConfigProperties provideConfigProperties(Plugin plugin) {
        CustomClassLoaderConstructor customClassLoaderConstructor = getCustomClassLoaderConstructor();
        Representer representer = getRepresenter();
        Yaml yaml = new Yaml(customClassLoaderConstructor, representer);
        return yaml.loadAs(getConfigInputStream(plugin), ConfigProperties.class);
    }

    private static CustomClassLoaderConstructor getCustomClassLoaderConstructor() {
        CustomClassLoaderConstructor customClassLoaderConstructor = Arrays.stream(CustomClassLoaderConstructor.class.getConstructors())
                .map(ConfigModule::newCustomClassLoaderConstructor)
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Could not find a constructor for CustomClassLoaderConstructor!"));
        customClassLoaderConstructor.getPropertyUtils().setSkipMissingProperties(true);
        return customClassLoaderConstructor;
    }

    private static CustomClassLoaderConstructor newCustomClassLoaderConstructor(Constructor<?> constructor) {
        try {
            if (constructor.getParameterCount() == 1 && constructor.getParameterTypes()[0].equals(ClassLoader.class)) {
                return (CustomClassLoaderConstructor) constructor.newInstance(ConfigProperties.class.getClassLoader());
            } else if (constructor.getParameterCount() == 2 && constructor.getParameterTypes()[0].equals(ClassLoader.class) && constructor.getParameterTypes()[1].equals(LoaderOptions.class)) {
                return (CustomClassLoaderConstructor) constructor.newInstance(ConfigProperties.class.getClassLoader(), new LoaderOptions());
            }
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Could not invoke constructor for CustomClassLoaderConstructor", e);
        }
        return null;
    }

    private static Representer getRepresenter() {
        Representer representer = new Representer(new DumperOptions());
        representer.getPropertyUtils().setSkipMissingProperties(true);
        return representer;
    }

    private static FileInputStream getConfigInputStream(Plugin plugin) {
        try {
            return new FileInputStream(new File(plugin.getDataFolder(), "config.yml"));
        } catch (FileNotFoundException e) {
            throw new IllegalStateException("Could not find config.yml", e);
        }
    }
}
