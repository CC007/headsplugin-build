package com.github.cc007.headsplugin.integration.daos.interfaces;

import com.github.cc007.headsplugin.api.business.domain.Head;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static com.github.npathai.hamcrestopt.OptionalMatchers.isEmpty;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresentAnd;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresentAndIs;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SearchableTest {

    @Spy
    Searchable searchable = new DummySearchable();

    @Test
    void getFirstHeadWithOneHead() {
        // prepare
        final var testSearchTerm = "SearchTerm";
        final var expected = Head.builder().name("expected").build();

        when(searchable.getHeads(testSearchTerm))
                .thenReturn(List.of(expected));

        // execute
        final var actual = searchable.getFirstHead(testSearchTerm);

        // verify
        assertThat(actual, isPresentAndIs(expected));
    }

    @Test
    void getFirstHeadWithMultipleHeads() {
        // prepare
        final var testSearchTerm = "SearchTerm";
        final var expected = Head.builder().name("expected").build();
        final var second = Head.builder().name("second").build();
        final var third = Head.builder().name("third").build();

        when(searchable.getHeads(testSearchTerm))
                .thenReturn(List.of(expected, second, third));

        // execute
        final var actual = searchable.getFirstHead(testSearchTerm);

        // verify
        assertThat(actual, isPresentAndIs(expected));
        assertThat(actual, isPresentAnd(is(not(second))));
        assertThat(actual, isPresentAnd(is(not(third))));
    }

    @Test
    void getFirstHeadWithNoHeads() {
        // prepare
        final var testSearchTerm = "SearchTerm";

        when(searchable.getHeads(testSearchTerm))
                .thenReturn(Collections.emptyList());

        // execute
        final var actual = searchable.getFirstHead(testSearchTerm);

        // verify
        assertThat(actual, isEmpty());
    }
}

class DummySearchable implements Searchable {

    @Override
    public List<Head> getHeads(String searchTerm) {
        return null;
    }

    @Override
    public String getDatabaseName() {
        return null;
    }
}