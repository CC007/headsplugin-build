package com.github.cc007.headsplugin.integration.database.mappers.from_entity;

import com.github.cc007.headsplugin.api.business.domain.Category;
import com.github.cc007.headsplugin.integration.database.entities.CategoryEntity;
import com.github.cc007.headsplugin.integration.database.entities.DatabaseEntity;

import org.apache.commons.collections4.Transformer;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class CategoryEntityToCategoryMapper implements Transformer<CategoryEntity, Category> {
    @Override
    public Category transform(CategoryEntity categoryEntity) {
        return Category.builder()
                .name(categoryEntity.getName())
                .sources(categoryEntity.getDatabases()
                        .stream()
                        .map(DatabaseEntity::getName)
                        .collect(Collectors.toList())
                )
                .build();
    }
}
