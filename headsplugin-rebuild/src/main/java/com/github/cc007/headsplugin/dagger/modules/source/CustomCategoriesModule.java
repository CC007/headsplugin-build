package com.github.cc007.headsplugin.dagger.modules.source;

import com.github.cc007.headsplugin.api.business.domain.Head;
import com.github.cc007.headsplugin.api.business.services.heads.HeadSearcher;
import com.github.cc007.headsplugin.config.properties.CategoriesProperties;
import com.github.cc007.headsplugin.config.properties.HeadspluginProperties;
import com.github.cc007.headsplugin.integration.daos.CustomCategoriesDao;
import com.github.cc007.headsplugin.integration.daos.FreshCoalDao;
import com.github.cc007.headsplugin.integration.rest.dto.freshcoal.SkinDto;
import com.github.cc007.headsplugin.integration.rest.feign.clients.FreshCoalClient;
import com.github.cc007.headsplugin.integration.rest.mappers.FreshCoalSkinDtoToHeadMapper;
import dagger.Module;
import dagger.Provides;
import org.apache.commons.collections4.Transformer;

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
