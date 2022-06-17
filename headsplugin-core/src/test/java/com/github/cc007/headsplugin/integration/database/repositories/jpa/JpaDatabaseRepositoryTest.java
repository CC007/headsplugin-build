package com.github.cc007.headsplugin.integration.database.repositories.jpa;

import com.github.cc007.headsplugin.integration.database.DummyDatabase;
import com.github.cc007.headsplugin.integration.database.entities.CategoryEntityMatcher;

import org.junit.jupiter.api.Test;

import static com.github.cc007.headsplugin.integration.database.entities.CategoryEntityMatcher.aCategoryEntityThat;
import static com.github.cc007.headsplugin.integration.database.entities.DatabaseEntityMatcher.aDatabaseEntityThat;
import static com.github.cc007.headsplugin.integration.database.entities.HeadEntityMatcher.aHeadEntityThat;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresentAnd;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;

class JpaDatabaseRepositoryTest {

    @Test
    void findByNameDatabase1() {
        DummyDatabase.runWithDB(headsPluginComponent -> {
            // prepare
            final var databaseRepository = headsPluginComponent.databaseRepository();

            // execute
            final var actual = databaseRepository.findByName("Database1");

            // verify
            assertThat(actual, isPresentAnd(is(aDatabaseEntityThat()
                    .hasName("Database1")
                    .hasCategories(containsInAnyOrder(
                            category1(),
                            category2()
                    ))
            )));
        });
    }

    @Test
    void findByNameDatabase2() {
        DummyDatabase.runWithDB(headsPluginComponent -> {
            // prepare
            final var databaseRepository = headsPluginComponent.databaseRepository();

            // execute
            final var actual = databaseRepository.findByName("Database2");

            // verify
            assertThat(actual, isPresentAnd(is(aDatabaseEntityThat()
                    .hasName("Database2")
                    .hasCategories(contains(
                            category1()
                    ))
            )));
        });
    }

    private CategoryEntityMatcher category1() {
        return aCategoryEntityThat()
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
                ));
    }

    private CategoryEntityMatcher category2() {
        return aCategoryEntityThat()
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
                ));
    }
}