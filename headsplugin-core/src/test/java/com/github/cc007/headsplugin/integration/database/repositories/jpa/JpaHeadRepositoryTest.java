package com.github.cc007.headsplugin.integration.database.repositories.jpa;

import com.github.cc007.headsplugin.integration.database.DummyDatabase;
import com.github.cc007.headsplugin.integration.database.entities.DatabaseEntity;
import com.github.cc007.headsplugin.integration.database.entities.HeadEntity;
import com.github.cc007.headsplugin.integration.database.entities.HeadEntityMatcher;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.github.cc007.headsplugin.integration.database.entities.CategoryEntityMatcher.aCategoryEntityThat;
import static com.github.cc007.headsplugin.integration.database.entities.DatabaseEntityMatcher.aDatabaseEntityThat;
import static com.github.cc007.headsplugin.integration.database.entities.HeadEntityMatcher.aHeadEntityThat;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresentAnd;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;

class JpaHeadRepositoryTest {

    private static String headOwner1_2;
    private static String headOwner2_1;

    @BeforeAll
    static void beforeAll() {
        DummyDatabase.runWithDB(headsPluginComponent -> {
            final var databaseRepository = headsPluginComponent.databaseRepository();

            Optional<DatabaseEntity> database1Optional = databaseRepository.findByName("Database1");
            Assumptions.assumeTrue(database1Optional.isPresent());
            Set<HeadEntity> heads = database1Optional.get().getHeads();

            Optional<HeadEntity> head1_2Optional = heads.stream()
                    .filter(headEntity -> "Head1_2".equals(headEntity.getName()))
                    .findAny();
            Assumptions.assumeTrue(head1_2Optional.isPresent());
            headOwner1_2 = head1_2Optional.get().getHeadOwner();

            Optional<HeadEntity> head2_1Optional = heads.stream()
                    .filter(headEntity -> "Head2_1".equals(headEntity.getName()))
                    .findAny();
            Assumptions.assumeTrue(head2_1Optional.isPresent());
            headOwner2_1 = head2_1Optional.get().getHeadOwner();
        });
    }

    @Test
    void findByHeadOwner1_2() {
        DummyDatabase.runWithDB(headsPluginComponent -> {
            // prepare
            final var headRepository = headsPluginComponent.headRepository();

            // execute
            final var actual = headRepository.findByHeadOwner(headOwner1_2);

            // verify
            assertThat(actual, isPresentAnd(is(head1_2())));
        });
    }

    @Test
    void findByHeadOwner2_1() {
        DummyDatabase.runWithDB(headsPluginComponent -> {
            // prepare
            final var headRepository = headsPluginComponent.headRepository();

            // execute
            final var actual = headRepository.findByHeadOwner(headOwner2_1);

            // verify
            assertThat(actual, isPresentAnd(is(head2_1())));
        });
    }


    @Test
    void findAllByHeadOwnerIn() {
        DummyDatabase.runWithDB(headsPluginComponent -> {
            // prepare
            final var headRepository = headsPluginComponent.headRepository();

            // execute
            final var actual = headRepository.findAllByHeadOwnerIn(List.of(
                    headOwner1_2,
                    headOwner2_1,
                    UUID.randomUUID().toString()
            ));

            // verify
            assertThat(actual, containsInAnyOrder(head1_2(), head2_1()));
        });
    }

    @Test
    void findAllHeadOwnersByHeadOwnerIn() {
        DummyDatabase.runWithDB(headsPluginComponent -> {
            // prepare
            final var headRepository = headsPluginComponent.headRepository();

            // execute
            final var actual = headRepository.findAllHeadOwnersByHeadOwnerIn(List.of(
                    headOwner1_2,
                    headOwner2_1,
                    UUID.randomUUID().toString()
            ));

            // verify
            assertThat(actual, containsInAnyOrder(headOwner1_2, headOwner2_1));
        });
    }

    @Test
    void findAllByDatabases_NameAndHeadOwnerIn() {
        DummyDatabase.runWithDB(headsPluginComponent -> {
            // prepare
            final var headRepository = headsPluginComponent.headRepository();

            // execute
            final var actual = headRepository.findAllByDatabases_NameAndHeadOwnerIn(
                    "Database2",
                    List.of(
                            headOwner1_2,
                            headOwner2_1,
                            UUID.randomUUID().toString()
                    )
            );

            // verify
            assertThat(actual, contains(head1_2()));
        });
    }

    @Test
    void findAllByNameIgnoreCaseContainingHead2() {
        DummyDatabase.runWithDB(headsPluginComponent -> {
            // prepare
            final var headRepository = headsPluginComponent.headRepository();

            // execute
            final var actual = headRepository.findAllByNameIgnoreCaseContaining("head2");

            // verify
            assertThat(actual, containsInAnyOrder(head2_1(), head2_2()));
        });
    }

    @Test
    void findAllByNameIgnoreCaseContainingHead() {
        DummyDatabase.runWithDB(headsPluginComponent -> {
            // prepare
            final var headRepository = headsPluginComponent.headRepository();

            // execute
            final var actual = headRepository.findAllByNameIgnoreCaseContaining("head");

            // verify
            assertThat(actual, containsInAnyOrder(head1_1(), head1_2(), head2_1(), head2_2()));
        });
    }

    private HeadEntityMatcher head1_1() {
        return aHeadEntityThat()
                .hasName("Head1_1")
                .hasValue("Value1_1")
                .hasDatabases(containsInAnyOrder(
                        aDatabaseEntityThat().hasName("Database1"),
                        aDatabaseEntityThat().hasName("Database2")
                ))
                .hasCategories(contains(
                        aCategoryEntityThat().hasName("Category1")
                ));
    }

    private HeadEntityMatcher head1_2() {
        return aHeadEntityThat()
                .hasName("Head1_2")
                .hasValue("Value1_2")
                .hasHeadOwner(headOwner1_2)
                .hasDatabases(containsInAnyOrder(
                        aDatabaseEntityThat().hasName("Database1"),
                        aDatabaseEntityThat().hasName("Database2")
                ))
                .hasCategories(contains(
                        aCategoryEntityThat().hasName("Category1")
                ));
    }

    private HeadEntityMatcher head2_1() {
        return aHeadEntityThat()
                .hasName("Head2_1")
                .hasValue("Value2_1")
                .hasHeadOwner(headOwner2_1)
                .hasDatabases(contains(
                        aDatabaseEntityThat().hasName("Database1")
                ))
                .hasCategories(contains(
                        aCategoryEntityThat().hasName("Category2")
                ));
    }

    private HeadEntityMatcher head2_2() {
        return aHeadEntityThat()
                .hasName("Head2_2")
                .hasValue("Value2_2")
                .hasDatabases(contains(
                        aDatabaseEntityThat().hasName("Database1")
                ))
                .hasCategories(contains(
                        aCategoryEntityThat().hasName("Category2")
                ));
    }
}