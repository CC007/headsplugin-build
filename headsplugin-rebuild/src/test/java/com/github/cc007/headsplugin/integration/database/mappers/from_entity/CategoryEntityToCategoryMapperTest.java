package com.github.cc007.headsplugin.integration.database.mappers.from_entity;

import com.github.cc007.headsplugin.ReflectionTestUtils;
import com.github.cc007.headsplugin.api.business.domain.Category;
import com.github.cc007.headsplugin.integration.database.entities.CategoryEntity;
import com.github.cc007.headsplugin.integration.database.entities.DatabaseEntity;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

class CategoryEntityToCategoryMapperTest {

    CategoryEntityToCategoryMapper categoryEntityToCategoryMapper = new CategoryEntityToCategoryMapper();

    @Test
    void transform() {
        // prepare
        String name = "TestName";
        String databaseName1 = "TestDatabaseName1";
        String databaseName2 = "TestDatabaseName2";

        DatabaseEntity databaseEntity1 = new DatabaseEntity();
        databaseEntity1.setName(databaseName1);
        DatabaseEntity databaseEntity2 = new DatabaseEntity();
        databaseEntity2.setName(databaseName2);

        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setName(name);
        ReflectionTestUtils.setDeclaredFieldValue(
                CategoryEntity.class,
                "databases",
                categoryEntity,
                new HashSet<>(Arrays.asList(
                        databaseEntity1, databaseEntity2
                ))
        );

        // execute
        Category actual = categoryEntityToCategoryMapper.transform(categoryEntity);

        // verify
        assertThat(actual, notNullValue());
        assertThat(actual.getName(), is(name));
        assertThat(actual.getSources(), containsInAnyOrder(databaseName1, databaseName2));
    }
}