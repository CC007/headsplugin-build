package com.github.cc007.headsplugin.business.services.heads.utils;

import com.github.cc007.headsplugin.api.business.domain.Head;

import lombok.val;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

class HeadUtilsImplTest {

    HeadUtilsImpl headUtils = new HeadUtilsImpl();

    @Test
    void getHeadOwnerStrings() {
        // prepare
        val head1_1 = Head.builder().name("Head1_1").headOwner(UUID.randomUUID()).build();
        val head1_2 = Head.builder().name("Head1_2").headOwner(UUID.randomUUID()).build();
        val head2_1 = Head.builder().name("Head2_1").headOwner(UUID.randomUUID()).build();
        val head2_2 = Head.builder().name("Head2_2").headOwner(UUID.randomUUID()).build();

        val heads = List.of(head1_1, head1_2, head2_1, head2_2);

        // execute
        val actual = headUtils.getHeadOwnerStrings(heads);

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
        val actualException = Assertions.assertThrows(NullPointerException.class,
                () -> headUtils.getHeadOwnerStrings(null)
        );

        // verify
        assertThat(actualException.getMessage(), containsString("is marked non-null but is null"));
    }

    @Test
    void getIntArrayFromUuid() {
        // prepare
        val uuid = UUID.fromString("01234567-89ab-cdef-fedc-ba9876543210");
        // execute
        val actual = headUtils.getIntArrayFromUuid(uuid);

        // verify
        assertThat(
                ArrayUtils.toObject(actual),
                is(arrayContaining(0x01234567, 0x89abcdef, 0xfedcba98, 0x76543210))
        );
    }

    @Test
    void getIntArrayFromUuidNull() {
        // prepare

        // execute
        val actualException = Assertions.assertThrows(NullPointerException.class,
                () -> headUtils.getIntArrayFromUuid(null)
        );

        // verify
        assertThat(actualException.getMessage(), containsString("is marked non-null but is null"));
    }

    @Test
    void isEmptyForMapWithKeysAndValues() {
        // prepare
        val testMap = Map.ofEntries(Map.entry("testEntry", List.of(1, 2, 3)));

        // execute
        val actual = headUtils.isEmpty(testMap);

        // verify
        assertThat(actual, is(false));
    }

    @Test
    void isEmptyForMapWithKeysWithoutValues() {
        // prepare
        val testMap = Map.<String, List<Integer>>ofEntries(Map.entry("testEntry", List.of()));

        // execute
        val actual = headUtils.isEmpty(testMap);

        // verify
        assertThat(actual, is(true));
    }

    @Test
    void isEmptyForMapWithoutKeys() {
        // prepare
        val testMap = Map.<String, List<Integer>>of();

        // execute
        val actual = headUtils.isEmpty(testMap);

        // verify
        assertThat(actual, is(true));
    }

    @Test
    void isEmptyNull() {
        // prepare

        // execute
        val actualException = Assertions.assertThrows(NullPointerException.class,
                () -> headUtils.isEmpty(null)
        );

        // verify
        assertThat(actualException.getMessage(), containsString("is marked non-null but is null"));
    }
}