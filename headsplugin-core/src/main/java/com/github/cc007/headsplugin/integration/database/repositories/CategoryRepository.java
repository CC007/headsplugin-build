package com.github.cc007.headsplugin.integration.database.repositories;

import com.github.cc007.headsplugin.integration.database.entities.CategoryEntity;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends Repository<CategoryEntity, Long> {

    List<CategoryEntity> findAll();

    Optional<CategoryEntity> findByName(String name);

    default CategoryEntity findByOrCreateFromName(String name) {
        final var category = findByName(name).orElseGet(() -> {
            CategoryEntity newCategory = manageNew();
            newCategory.setName(name);
            return newCategory;
        });
        if (category.getLastUpdated() == null) {
            category.setLastUpdated(LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC));
        }
        return category;
    }
}
