package com.github.cc007.headsplugin.integration.database.repositories.jpa;

import com.github.cc007.headsplugin.dagger.DaggerHeadsPluginComponent;
import com.github.cc007.headsplugin.dagger.HeadsPluginComponent;
import com.github.cc007.headsplugin.integration.database.DatabaseTestSetup;

import lombok.val;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.github.cc007.headsplugin.integration.database.entities.HeadEntityMatcher.aHeadEntityThat;
import static com.github.cc007.headsplugin.integration.database.entities.SearchEntityMatcher.aSearchEntityThat;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresentAnd;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;

class JpaSearchRepositoryTest {

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
    void findBySearchTermSearch1() {
        // prepare
        val searchRepository = headsPluginComponent.searchRepository();

        // execute
        val actual = searchRepository.findBySearchTerm("Search1");

        // verify
        assertThat(actual, isPresentAnd(is(aSearchEntityThat()
                .hasSearchTerm("Search1")
                .hasHeads(containsInAnyOrder(
                        aHeadEntityThat()
                                .hasName("Head1_2")
                                .hasValue("Value1_2"),
                        aHeadEntityThat()
                                .hasName("Head2_2")
                                .hasValue("Value2_2")
                ))
        )));
    }
}