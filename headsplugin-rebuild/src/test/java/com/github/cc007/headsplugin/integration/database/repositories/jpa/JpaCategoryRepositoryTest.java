package com.github.cc007.headsplugin.integration.database.repositories.jpa;

import com.github.cc007.headsplugin.integration.database.DummyDatabase;

import lombok.val;
import org.junit.jupiter.api.Test;

import static com.github.cc007.headsplugin.integration.database.entities.CategoryEntityMatcher.aCategoryEntityThat;
import static com.github.cc007.headsplugin.integration.database.entities.DatabaseEntityMatcher.aDatabaseEntityThat;
import static com.github.cc007.headsplugin.integration.database.entities.HeadEntityMatcher.aHeadEntityThat;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresentAnd;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;

class JpaCategoryRepositoryTest {

    @Test
    void findAll() {
        DummyDatabase.runWithDB(headsPluginComponent -> {
            // prepare
            final var categoryRepository = headsPluginComponent.categoryRepository();

            // execute
            final var actual = categoryRepository.findAll();

            // verify
            assertThat(actual, contains(
                    aCategoryEntityThat()
                            .hasName("Category1")
                            .hasDatabases(containsInAnyOrder(
                                    aDatabaseEntityThat().hasName("Database1"),
                                    aDatabaseEntityThat().hasName("Database2")
                            ))
                            .hasHeads(containsInAnyOrder(
                                    aHeadEntityThat()
                                            .hasName("Head1_1")
                                            .hasValue("Value1_1"),
                                    aHeadEntityThat()
                                            .hasName("Head1_2")
                                            .hasValue("Value1_2")
                            )),
                    aCategoryEntityThat()
                            .hasName("Category2")
                            .hasDatabases(containsInAnyOrder(
                                    aDatabaseEntityThat().hasName("Database1")
                            ))
                            .hasHeads(containsInAnyOrder(
                                    aHeadEntityThat()
                                            .hasName("Head2_1")
                                            .hasValue("Value2_1"),
                                    aHeadEntityThat()
                                            .hasName("Head2_2")
                                            .hasValue("Value2_2")
                            ))
            ));
        });
    }

    @Test
    void findByNameCategory1() {
        DummyDatabase.runWithDB(headsPluginComponent -> {
            // prepare
            final var categoryRepository = headsPluginComponent.categoryRepository();

            // execute
            final var actual = categoryRepository.findByName("Category1");

            // verify
            assertThat(actual, isPresentAnd(is(aCategoryEntityThat()
                    .hasName("Category1")
                    .hasDatabases(containsInAnyOrder(
                            aDatabaseEntityThat().hasName("Database1"),
                            aDatabaseEntityThat().hasName("Database2")
                    ))
                    .hasHeads(containsInAnyOrder(
                            aHeadEntityThat()
                                    .hasName("Head1_1")
                                    .hasValue("Value1_1"),
                            aHeadEntityThat()
                                    .hasName("Head1_2")
                                    .hasValue("Value1_2")
                    ))
            )));
        });
    }

    @Test
    void findByNameCategory2() {
        DummyDatabase.runWithDB(headsPluginComponent -> {
            // prepare
            val categoryRepository = headsPluginComponent.categoryRepository();

            // execute
            val actual = categoryRepository.findByName("Category2");

            // verify
            assertThat(actual, isPresentAnd(is(aCategoryEntityThat()
                    .hasName("Category2")
                    .hasDatabases(containsInAnyOrder(
                            aDatabaseEntityThat().hasName("Database1")
                    ))
                    .hasHeads(containsInAnyOrder(
                            aHeadEntityThat()
                                    .hasName("Head2_1")
                                    .hasValue("Value2_1"),
                            aHeadEntityThat()
                                    .hasName("Head2_2")
                                    .hasValue("Value2_2")
                    ))
            )));
        });
    }
}