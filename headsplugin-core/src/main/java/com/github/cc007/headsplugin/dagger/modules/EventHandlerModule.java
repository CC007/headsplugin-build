package com.github.cc007.headsplugin.dagger.modules;

import com.github.cc007.headsplugin.config.properties.CategoriesProperties;
import com.github.cc007.headsplugin.handlers.CategoriesUpdatedEventListener;
import com.github.cc007.headsplugin.handlers.CategoryUpdatedEventListener;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class EventHandlerModule {
    @Provides
    @Singleton
    static CategoryUpdatedEventListener provideCategoryUpdatedEventListener(CategoriesProperties categoriesProperties) {
        return new CategoryUpdatedEventListener(categoriesProperties);
    }

    @Provides
    @Singleton
    static CategoriesUpdatedEventListener provideCategoriesUpdatedEventListener(CategoriesProperties categoriesProperties) {
        return new CategoriesUpdatedEventListener(categoriesProperties);
    }
}
