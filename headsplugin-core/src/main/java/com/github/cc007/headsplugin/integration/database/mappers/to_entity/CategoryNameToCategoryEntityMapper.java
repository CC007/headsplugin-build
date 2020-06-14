package com.github.cc007.headsplugin.integration.database.mappers.to_entity;

import com.github.cc007.headsplugin.integration.database.entities.CategoryEntity;
import com.github.cc007.headsplugin.integration.database.repositories.CategoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.apache.commons.collections4.Transformer;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
@RequiredArgsConstructor
@Log4j2
public class CategoryNameToCategoryEntityMapper implements Transformer<String, CategoryEntity> {

    private final CategoryRepository categoryRepository;

    @Override
    public CategoryEntity transform(String categoryName) {
        val optionalCategory = categoryRepository.findByName(categoryName);

        CategoryEntity category;
        if (!optionalCategory.isPresent()) {
            log.info("Found new category: " + categoryName);
            category = new CategoryEntity();
            category.setName(categoryName);
            category.setLastUpdated(LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC));
        } else {
            category = optionalCategory.get();
        }
        return categoryRepository.save(category);
    }
}
