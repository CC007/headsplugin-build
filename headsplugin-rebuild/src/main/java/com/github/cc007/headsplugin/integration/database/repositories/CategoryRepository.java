package com.github.cc007.headsplugin.integration.database.repositories;

import com.github.cc007.headsplugin.integration.database.entities.CategoryEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends Repository<CategoryEntity, Long> {

    List<CategoryEntity> findAll();

    Optional<CategoryEntity> findByName(String name);

    default CategoryEntity findByOrCreateFromName(String name) {
        return findByName(name).orElseGet(() -> {
            CategoryEntity newCategory = manageNew();
            newCategory.setName(name);
            newCategory.setLastUpdated(LocalDateTime.now());
            return newCategory;
        });
    }
}
