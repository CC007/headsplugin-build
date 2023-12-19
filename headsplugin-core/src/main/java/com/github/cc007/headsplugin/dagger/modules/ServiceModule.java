package com.github.cc007.headsplugin.dagger.modules;

import com.github.cc007.headsplugin.api.business.services.Profiler;
import com.github.cc007.headsplugin.api.business.services.heads.utils.CategoryUtils;
import com.github.cc007.headsplugin.api.business.services.heads.utils.HeadUtils;
import com.github.cc007.headsplugin.business.services.OwnerProfileService;
import com.github.cc007.headsplugin.business.services.ProfilerImpl;
import com.github.cc007.headsplugin.business.services.heads.utils.CategoryUtilsImpl;
import com.github.cc007.headsplugin.business.services.heads.utils.HeadUtilsImpl;
import com.github.cc007.headsplugin.config.properties.ConfigProperties;
import com.github.cc007.headsplugin.integration.daos.CustomCategoriesDao;
import com.github.cc007.headsplugin.integration.daos.FreshCoalDao;
import com.github.cc007.headsplugin.integration.daos.MineSkinDao;
import com.github.cc007.headsplugin.integration.daos.MinecraftHeadsDao;
import com.github.cc007.headsplugin.integration.daos.interfaces.Categorizable;
import com.github.cc007.headsplugin.integration.daos.interfaces.Creatable;
import com.github.cc007.headsplugin.integration.daos.interfaces.Searchable;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.ElementsIntoSet;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;

import javax.inject.Singleton;
import java.util.Set;

@Module
@Log4j2
public abstract class ServiceModule {

    @Provides
    @Singleton
    static OwnerProfileService provideOwnerProfileHelper() {
        return new OwnerProfileService();
    }

    @Provides
    @Singleton
    static Profiler provideProfiler(ConfigProperties configProperties) {
        String defaultLogLevelName = configProperties.getProfiler().getDefaultLogLevel();
        Level defaultLogLevel = Level.toLevel(defaultLogLevelName, Level.DEBUG);
        return new ProfilerImpl(defaultLogLevel);
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
            MinecraftHeadsDao minecraftHeadsDao,
            CustomCategoriesDao customCategoriesDao
    ) {
        return Set.of(freshCoalDao, minecraftHeadsDao, customCategoriesDao);
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
