package com.github.cc007.headsplugin.dagger.modules.api;

import com.github.cc007.headsplugin.api.business.domain.Category;
import com.github.cc007.headsplugin.api.business.domain.Head;
import com.github.cc007.headsplugin.api.business.services.Profiler;
import com.github.cc007.headsplugin.api.business.services.heads.CategorySearcher;
import com.github.cc007.headsplugin.api.business.services.heads.CategoryUpdater;
import com.github.cc007.headsplugin.api.business.services.heads.HeadCreator;
import com.github.cc007.headsplugin.api.business.services.heads.HeadPlacer;
import com.github.cc007.headsplugin.api.business.services.heads.HeadSearcher;
import com.github.cc007.headsplugin.api.business.services.heads.HeadToItemstackMapper;
import com.github.cc007.headsplugin.api.business.services.heads.HeadUpdater;
import com.github.cc007.headsplugin.api.business.services.heads.utils.CategoryUtils;
import com.github.cc007.headsplugin.api.business.services.heads.utils.HeadUtils;
import com.github.cc007.headsplugin.business.services.NbtService;
import com.github.cc007.headsplugin.business.services.heads.CategorySearcherImpl;
import com.github.cc007.headsplugin.business.services.heads.CategoryUpdaterImpl;
import com.github.cc007.headsplugin.business.services.heads.HeadCreatorImpl;
import com.github.cc007.headsplugin.business.services.heads.HeadPlacerImpl;
import com.github.cc007.headsplugin.business.services.heads.HeadSearcherImpl;
import com.github.cc007.headsplugin.business.services.heads.HeadToItemstackMapperImpl;
import com.github.cc007.headsplugin.business.services.heads.HeadUpdaterImpl;
import com.github.cc007.headsplugin.config.properties.CategoriesProperties;
import com.github.cc007.headsplugin.config.properties.HeadspluginProperties;
import com.github.cc007.headsplugin.integration.daos.interfaces.Creatable;
import com.github.cc007.headsplugin.integration.daos.interfaces.Searchable;
import com.github.cc007.headsplugin.integration.database.entities.CategoryEntity;
import com.github.cc007.headsplugin.integration.database.entities.HeadEntity;
import com.github.cc007.headsplugin.integration.database.repositories.CategoryRepository;
import com.github.cc007.headsplugin.integration.database.repositories.DatabaseRepository;
import com.github.cc007.headsplugin.integration.database.repositories.HeadRepository;
import com.github.cc007.headsplugin.integration.database.repositories.SearchRepository;
import com.github.cc007.headsplugin.integration.database.transaction.Transaction;

import dagger.Module;
import dagger.Provides;
import org.apache.commons.collections4.Transformer;
import org.bukkit.plugin.Plugin;

import javax.inject.Singleton;
import java.util.Set;

@Module
public abstract class ApiServiceModule {

    @Provides
    @Singleton
    static CategorySearcher provideCategorySearcher(
            CategoryRepository categoryRepository,
            Transformer<CategoryEntity, Category> categoryEntityToCategoryMapper,
            Transformer<HeadEntity, Head> headEntityToHeadMapper,
            Transaction transaction,
            Profiler profiler
    ) {
        return new CategorySearcherImpl(categoryRepository, categoryEntityToCategoryMapper, headEntityToHeadMapper, transaction, profiler);
    }

    @Provides
    @Singleton
    static CategoryUpdater provideCategoryUpdater(
            HeadUpdater headUpdater,
            CategoryUtils categoryUtils,
            HeadUtils headUtils,
            CategoryRepository categoryRepository,
            DatabaseRepository databaseRepository,
            Plugin plugin,
            CategoriesProperties categoriesProperties,
            Transaction transaction,
            Profiler profiler
    ) {
        return new CategoryUpdaterImpl(headUpdater, categoryUtils, headUtils, categoryRepository, databaseRepository, plugin, categoriesProperties, transaction, profiler);
    }

    @Provides
    @Singleton
    static HeadCreator provideHeadCreator(
            Set<Creatable> creatables,
            HeadUpdater headUpdater,
            DatabaseRepository databaseRepository,
            Transaction transaction
    ) {
        return new HeadCreatorImpl(creatables, headUpdater, databaseRepository, transaction);
    }

    @Provides
    @Singleton
    static HeadPlacer provideHeadPlacer(
            HeadUtils headUtils,
            NbtService nbtService
    ) {
        return new HeadPlacerImpl(headUtils, nbtService);
    }

    @Provides
    @Singleton
    static HeadSearcher provideHeadSearcher(
            Set<Searchable> searchables,
            HeadUpdater headUpdater,
            HeadUtils headUtils,
            Transformer<HeadEntity, Head> headEntityToHeadMapper,
            HeadRepository headRepository,
            SearchRepository searchRepository,
            DatabaseRepository databaseRepository,
            HeadspluginProperties headspluginProperties,
            Transaction transaction,
            Profiler profiler
    ) {
        return new HeadSearcherImpl(searchables, headUpdater, headUtils, headEntityToHeadMapper, headRepository, searchRepository, databaseRepository, headspluginProperties, transaction, profiler);
    }

    @Provides
    @Singleton
    static HeadToItemstackMapper provideHeadToItemstackMapper(
            HeadUtils headUtils,
            NbtService nbtService
    ) {
        return new HeadToItemstackMapperImpl(headUtils, nbtService);
    }

    @Provides
    @Singleton
    static HeadUpdater provideHeadUpdater(
            HeadRepository headRepository,
            DatabaseRepository databaseRepository,
            HeadUtils headUtils,
            Transaction transaction
    ) {
        return new HeadUpdaterImpl(headRepository, databaseRepository, headUtils, transaction);
    }
}
