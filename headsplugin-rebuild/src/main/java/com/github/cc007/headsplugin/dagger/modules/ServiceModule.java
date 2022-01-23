package com.github.cc007.headsplugin.dagger.modules;

import com.github.cc007.headsplugin.api.business.services.Profiler;
import com.github.cc007.headsplugin.api.business.services.heads.utils.CategoryUtils;
import com.github.cc007.headsplugin.api.business.services.heads.utils.HeadUtils;
import com.github.cc007.headsplugin.business.services.NbtService;
import com.github.cc007.headsplugin.business.services.ProfilerImpl;
import com.github.cc007.headsplugin.business.services.heads.utils.CategoryUtilsImpl;
import com.github.cc007.headsplugin.business.services.heads.utils.HeadUtilsImpl;
import com.github.cc007.headsplugin.config.properties.ConfigProperties;
import com.github.cc007.headsplugin.integration.daos.FreshCoalDao;
import com.github.cc007.headsplugin.integration.daos.MineSkinDao;
import com.github.cc007.headsplugin.integration.daos.MinecraftHeadsDao;
import com.github.cc007.headsplugin.integration.daos.interfaces.Categorizable;
import com.github.cc007.headsplugin.integration.daos.interfaces.Creatable;
import com.github.cc007.headsplugin.integration.daos.interfaces.Searchable;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.ElementsIntoSet;
import org.apache.logging.log4j.Level;

import javax.inject.Singleton;
import java.util.Set;

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

    @Provides
    @Singleton
    static HeadUtils provideHeadUtils() {
        return new HeadUtilsImpl();
    }

    @Provides
    @Singleton
    static CategoryUtils provideCategoryUtils(Set<Categorizable> categorizables) {
        return new CategoryUtilsImpl(categorizables);
    }

    @Provides
    @Singleton
    @ElementsIntoSet
    static Set<Categorizable> provideCategorizables(
            FreshCoalDao freshCoalDao,
            MinecraftHeadsDao minecraftHeadsDao
    ) {
        return Set.of(freshCoalDao, minecraftHeadsDao);
    }

    @Provides
    @Singleton
    @ElementsIntoSet
    static Set<Searchable> provideSearchables(
            FreshCoalDao freshCoalDao,
            MineSkinDao mineSkinDao
    ) {
        return Set.of(freshCoalDao, mineSkinDao);
    }

    @Provides
    @Singleton
    @ElementsIntoSet
    static Set<Creatable> provideCreatables(MineSkinDao mineSkinDao) {
        return Set.of(mineSkinDao);
    }
}
