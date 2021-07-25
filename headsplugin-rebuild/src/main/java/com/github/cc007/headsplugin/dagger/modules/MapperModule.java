package com.github.cc007.headsplugin.dagger.modules;

import com.github.cc007.headsplugin.api.business.domain.Category;
import com.github.cc007.headsplugin.integration.database.entities.CategoryEntity;
import com.github.cc007.headsplugin.integration.database.mappers.from_entity.CategoryEntityToCategoryMapper;

import dagger.Module;
import dagger.Provides;
import org.apache.commons.collections4.Transformer;

import javax.inject.Singleton;

@Module
public abstract class MapperModule {

    @Provides
    @Singleton
    static Transformer<CategoryEntity, Category> provideCategoryEntityToCategoryMapper() {
        return new CategoryEntityToCategoryMapper();
    }
}
