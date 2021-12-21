package com.github.cc007.headsplugin.integration.database.repositories.jpa;

import com.github.cc007.headsplugin.integration.database.entities.CategoryEntity;
import com.github.cc007.headsplugin.integration.database.repositories.CategoryRepository;
import com.github.cc007.headsplugin.integration.database.services.ManagedEntityService;
import com.github.cc007.headsplugin.integration.database.services.QueryService;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class JpaCategoryRepository implements CategoryRepository {

    private final QueryService queryService;
    private final ManagedEntityService managedEntityService;

    @Override
    public List<CategoryEntity> findAll() {
        return queryService.findAll(CategoryEntity.class);
    }

    @Override
    public Optional<CategoryEntity> findByName(String name) {
        return queryService.findByProperty(CategoryEntity.class, "name", name);
    }

    @Override
    public CategoryEntity manageNew() {
        return managedEntityService.manageNew(CategoryEntity.class);
    }

    @Override
    public CategoryEntity manage(CategoryEntity entity) {
        return managedEntityService.manage(entity);
    }
}
