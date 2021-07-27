package com.github.cc007.headsplugin.integration.database.repositories;

import com.github.cc007.headsplugin.integration.database.entities.CategoryEntity;

import java.util.Optional;

public interface CategoryRepository extends Repository<CategoryEntity, Long> {

    Optional<CategoryEntity> findByName(String name);
}
