package com.github.cc007.headsplugin.integration.database.repositories.jpa;

import com.github.cc007.headsplugin.integration.database.DummyDatabase;

import lombok.val;
import org.junit.jupiter.api.Test;

import static com.github.cc007.headsplugin.integration.database.entities.HeadEntityMatcher.aHeadEntityThat;
import static com.github.cc007.headsplugin.integration.database.entities.SearchEntityMatcher.aSearchEntityThat;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresentAnd;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;

class JpaSearchRepositoryTest {

    @Test
    void findBySearchTermSearch1() {
        DummyDatabase.runWithDB(headsPluginComponent -> {
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
        });
    }
}