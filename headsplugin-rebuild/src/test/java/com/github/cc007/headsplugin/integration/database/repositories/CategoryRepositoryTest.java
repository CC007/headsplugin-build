package com.github.cc007.headsplugin.integration.database.repositories;

import com.github.cc007.headsplugin.integration.database.entities.CategoryEntity;

import lombok.val;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.github.cc007.headsplugin.integration.database.entities.CategoryEntityMatcher.aCategoryEntityThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryRepositoryTest {

    @Spy
    CategoryRepository categoryRepository = new DummyCategoryRepository();

    @Test
    void findByOrCreateFromNameCategoryNotFound() {
        // prepare
        val testCategoryName = "CategoryName";
        val testCategoryEntity = new CategoryEntity();

        when(categoryRepository.findByName(testCategoryName))
                .thenReturn(Optional.empty());
        when(categoryRepository.manageNew())
                .thenReturn(testCategoryEntity);

        // execute
        val actual = categoryRepository.findByOrCreateFromName(testCategoryName);

        // verify
        assertThat(actual, is(aCategoryEntityThat()
                .hasName(testCategoryName)
                .hasLastUpdated(Matchers.notNullValue())));
    }

    @Test
    void findByOrCreateFromNameCategoryFound() {
        // prepare
        val testCategoryName = "CategoryName";
        val testLastUpdated = LocalDateTime.now();

        val testCategoryEntity = new CategoryEntity();
        testCategoryEntity.setName(testCategoryName);
        testCategoryEntity.setLastUpdated(testLastUpdated);

        when(categoryRepository.findByName(testCategoryName))
                .thenReturn(Optional.of(testCategoryEntity));

        // execute
        val actual = categoryRepository.findByOrCreateFromName(testCategoryName);

        // verify
        assertThat(actual, is(aCategoryEntityThat()
                .hasName(testCategoryName)
                .hasLastUpdated(testLastUpdated)));
    }
}

class DummyCategoryRepository implements CategoryRepository {

    @Override
    public List<CategoryEntity> findAll() {
        return null;
    }

    @Override
    public Optional<CategoryEntity> findByName(String name) {
        return Optional.empty();
    }

    @Override
    public CategoryEntity manageNew() {
        return null;
    }

    @Override
    public CategoryEntity manage(CategoryEntity entity) {
        return null;
    }
}