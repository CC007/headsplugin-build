package com.github.cc007.headsplugin.dagger.modules;

import com.github.cc007.headsplugin.HeadsPlugin;
import com.github.cc007.headsplugin.config.properties.CategoriesProperties;
import com.github.cc007.headsplugin.config.properties.ConfigProperties;

import dagger.Module;
import dagger.Provides;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.introspector.PropertyUtils;

import java.util.Optional;

@Module
public abstract class HeadsPluginModule {

    @Provides
    static HeadsPlugin provideHeadsPlugin() {
        Optional<HeadsPlugin> headsPluginOptional = HeadsPlugin.getPlugin();
        if (!headsPluginOptional.isPresent()) {
            throw new IllegalStateException("HeadsPluginAPI has not been enabled yet");
        }
        return headsPluginOptional.get();
    }
    
    @Provides
    static ConfigProperties provideConfigProperties(HeadsPlugin headsPlugin) {
        CustomClassLoaderConstructor configPropertiesClassLoader = new CustomClassLoaderConstructor(ConfigProperties.class.getClassLoader());
        configPropertiesClassLoader.getPropertyUtils().setSkipMissingProperties(true);
        Yaml yaml = new Yaml(configPropertiesClassLoader);
        return yaml.loadAs(headsPlugin.getResource("config.yml"), ConfigProperties.class);
    }

    @Provides
    static CategoriesProperties provideCategoriesProperties(ConfigProperties configProperties) {
        return configProperties.getHeadsplugin().getCategories();
    }
//
//    @Binds
//    public abstract HeadCreator bindHeadCreator(HeadCreatorImpl headCreatorImpl);
//
//    @Provides
//    @ElementsIntoSet
//    public static Set<DatabaseSource> provideDatabaseSources(
//            FreshCoalDao freshCoalDao,
//            MinecraftHeadsDao minecraftHeadsDao
//    ) {
//        return new HashSet<>(Arrays.asList(
//                freshCoalDao,
//                minecraftHeadsDao
//        ));
//    }
//
//    @Provides
//    @ElementsIntoSet
//    public static Set<Searchable> provideSearchables(
//            FreshCoalDao freshCoalDao,
//            MineSkinDao mineSkinDao
//    ) {
//        return new HashSet<>(Arrays.asList(
//                freshCoalDao,
//                mineSkinDao
//        ));
//    }
//
//    @Provides
//    @ElementsIntoSet
//    public static Set<Creatable> provideCreatables(MineSkinDao mineSkinDao) {
//        return new HashSet<>(Collections.singletonList(
//                mineSkinDao
//        ));
//    }
}
