package com.github.cc007.headsplugin.business.services.heads;

import com.github.cc007.headsplugin.api.business.domain.Head;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;

class HeadUtilsImplTest {

    HeadUtilsImpl headUtils = new HeadUtilsImpl();

    @Test
    void flattenHeads() {
        // prepare
        Head head1_1 = Head.builder().name("Head1_1").build();
        Head head1_2 = Head.builder().name("Head1_2").build();
        Head head2_1 = Head.builder().name("Head2_1").build();
        Head head2_2 = Head.builder().name("Head2_2").build();

        Collection<List<Head>> heads = Arrays.asList(
                Arrays.asList(head1_1, head1_2),
                Arrays.asList(head2_1, head2_2)
        );

        // execute
        List<Head> actual = headUtils.flattenHeads(heads);

        // verify
        assertThat(actual, containsInAnyOrder(head1_1, head1_2, head2_1, head2_2));
    }

    @Test
    void getHeadOwnerStrings() {
        // prepare
        Head head1_1 = Head.builder().name("Head1_1").headOwner(UUID.randomUUID()).build();
        Head head1_2 = Head.builder().name("Head1_2").headOwner(UUID.randomUUID()).build();
        Head head2_1 = Head.builder().name("Head2_1").headOwner(UUID.randomUUID()).build();
        Head head2_2 = Head.builder().name("Head2_2").headOwner(UUID.randomUUID()).build();

        Collection<Head> heads = Arrays.asList(head1_1, head1_2, head2_1, head2_2);

        // execute
        List<String> actual = headUtils.getHeadOwnerStrings(heads);

        // verify
        assertThat(actual, contains(
                head1_1.getHeadOwner().toString(),
                head1_2.getHeadOwner().toString(),
                head2_1.getHeadOwner().toString(),
                head2_2.getHeadOwner().toString()
        ));
    }

    @Test
    void getIntArrayFromUuid() {
        // prepare
        UUID uuid = UUID.fromString("01234567-89ab-cdef-fedc-ba9876543210");
        // execute
        int[] actual = headUtils.getIntArrayFromUuid(uuid);

        // verify
        assertThat(
                ArrayUtils.toObject(actual),
                arrayContaining(0x01234567, 0x89abcdef, 0xfedcba98, 0x76543210)
        );
    }
}