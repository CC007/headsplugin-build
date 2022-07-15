package com.github.cc007.headsplugin.business.services.heads.utils;

import com.github.cc007.headsplugin.api.business.domain.Head;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

class HeadUtilsImplTest {

    HeadUtilsImpl headUtils = new HeadUtilsImpl();

    @Test
    void getHeadOwnerStrings() {
        // prepare
        final var head1_1 = Head.builder().name("Head1_1").headOwner(UUID.randomUUID()).build();
        final var head1_2 = Head.builder().name("Head1_2").headOwner(UUID.randomUUID()).build();
        final var head2_1 = Head.builder().name("Head2_1").headOwner(UUID.randomUUID()).build();
        final var head2_2 = Head.builder().name("Head2_2").headOwner(UUID.randomUUID()).build();

        final var heads = List.of(head1_1, head1_2, head2_1, head2_2);

        // execute
        final var actual = headUtils.getHeadOwnerStrings(heads);

        // verify
        assertThat(actual, contains(
                head1_1.getHeadOwner().toString(),
                head1_2.getHeadOwner().toString(),
                head2_1.getHeadOwner().toString(),
                head2_2.getHeadOwner().toString()
        ));
    }

    @Test
    void getHeadOwnerStringsNull() {
        // prepare

        // execute
        final var actualException = Assertions.assertThrows(NullPointerException.class,
                () -> headUtils.getHeadOwnerStrings(null)
        );

        // verify
        assertThat(actualException.getMessage(), containsString("is marked non-null but is null"));
    }

    @Test
    void isEmptyForMapWithKeysAndValues() {
        // prepare
        final var testMap = Map.ofEntries(Map.entry("testEntry", List.of(1, 2, 3)));

        // execute
        final var actual = headUtils.isEmpty(testMap);

        // verify
        assertThat(actual, is(false));
    }

    @Test
    void isEmptyForMapWithKeysWithoutValues() {
        // prepare
        final var testMap = Map.<String, List<Integer>>ofEntries(Map.entry("testEntry", List.of()));

        // execute
        final var actual = headUtils.isEmpty(testMap);

        // verify
        assertThat(actual, is(true));
    }

    @Test
    void isEmptyForMapWithoutKeys() {
        // prepare
        final var testMap = Map.<String, List<Integer>>of();

        // execute
        final var actual = headUtils.isEmpty(testMap);

        // verify
        assertThat(actual, is(true));
    }

    @Test
    void isEmptyNull() {
        // prepare

        // execute
        final var actualException = Assertions.assertThrows(NullPointerException.class,
                () -> headUtils.isEmpty(null)
        );

        // verify
        assertThat(actualException.getMessage(), containsString("is marked non-null but is null"));
    }
}