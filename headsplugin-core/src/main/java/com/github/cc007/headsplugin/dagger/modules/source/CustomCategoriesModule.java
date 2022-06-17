package com.github.cc007.headsplugin.dagger.modules.source;

import com.github.cc007.headsplugin.api.business.services.heads.HeadSearcher;
import com.github.cc007.headsplugin.config.properties.CategoriesProperties;
import com.github.cc007.headsplugin.integration.daos.CustomCategoriesDao;

import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public abstract class CustomCategoriesModule {

    @Provides
    @Singleton
    static CustomCategoriesDao provideCustomCategoriesDao(
            CategoriesProperties categoriesProperties,
            HeadSearcher headSearcher
    ) {
        return new CustomCategoriesDao(categoriesProperties, headSearcher);
    }
}
