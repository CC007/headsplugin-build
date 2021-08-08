package com.github.cc007.headsplugin.integration.database.repositories.jpa;

import com.github.cc007.headsplugin.dagger.DaggerHeadsPluginComponent;
import com.github.cc007.headsplugin.dagger.HeadsPluginComponent;
import com.github.cc007.headsplugin.integration.database.DatabaseTestSetup;
import com.github.cc007.headsplugin.integration.database.entities.CategoryEntityMatcher;

import static com.github.cc007.headsplugin.integration.database.entities.CategoryEntityMatcher.aCategoryEntityThat;
import static com.github.cc007.headsplugin.integration.database.entities.DatabaseEntityMatcher.aDatabaseEntityThat;
import static com.github.cc007.headsplugin.integration.database.entities.HeadEntityMatcher.aHeadEntityThat;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresentAnd;
import lombok.val;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class JpaDatabaseRepositoryTest {

    private static HeadsPluginComponent headsPluginComponent;

    @BeforeAll
    static void beforeAll() {
        headsPluginComponent = DaggerHeadsPluginComponent.create();
        DatabaseTestSetup.setUpDB(headsPluginComponent);
    }

    @AfterAll
    static void afterAll() {
        DatabaseTestSetup.tearDownDB(headsPluginComponent, true);
    }

    @Test
    void findByNameDatabase1() {
        // prepare
        val databaseRepository = headsPluginComponent.databaseRepository();

        // execute
        val actual = databaseRepository.findByName("Database1");

        // verify
        assertThat(actual, isPresentAnd(is(aDatabaseEntityThat()
                .hasName("Database1")
                .hasCategories(containsInAnyOrder(
                        category1(),
                        category2()
                ))

        )));
    }

    @Test
    void findByNameDatabase2() {
        // prepare
        val databaseRepository = headsPluginComponent.databaseRepository();

        // execute
        val actual = databaseRepository.findByName("Database2");

        // verify
        assertThat(actual, isPresentAnd(is(aDatabaseEntityThat()
                .hasName("Database2")
                .hasCategories(contains(category1()
                ))

        )));
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