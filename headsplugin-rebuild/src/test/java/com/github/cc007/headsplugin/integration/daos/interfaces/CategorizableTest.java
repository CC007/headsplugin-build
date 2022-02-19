package com.github.cc007.headsplugin.integration.daos.interfaces;

import com.github.cc007.headsplugin.api.business.domain.Head;

import lombok.NonNull;
import lombok.val;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategorizableTest {

    @Spy
    Categorizable categorizable = new DummyCategorizable();

    @Test
    void getAllCategoryHeads() {
        // prepare
        final var testCategoryName1 = "CategoryName1";
        final var testCategoryName2 = "CategoryName2";
        final var testCategoryName3 = "CategoryName3";
        final var testCategoryHead11 = Head.builder().name("CategoryHead11").build();
        final var testCategoryHead12 = Head.builder().name("CategoryHead12").build();
        final var testCategoryHead31 = Head.builder().name("CategoryHead31").build();

        when(categorizable.getCategoryNames())
                .thenReturn(List.of(testCategoryName1, testCategoryName2, testCategoryName3));

        when(categorizable.getCategoryHeads(testCategoryName1))
                .thenReturn(List.of(testCategoryHead11, testCategoryHead12));
        when(categorizable.getCategoryHeads(testCategoryName2))
                .thenReturn(Collections.emptyList());
        when(categorizable.getCategoryHeads(testCategoryName3))
                .thenReturn(List.of(testCategoryHead31));

        // execute
        final var actual = categorizable.getAllCategoryHeads();

        // verify
        assertThat(actual, Matchers.containsInAnyOrder(testCategoryHead11, testCategoryHead12, testCategoryHead31));
    }

    @Test
    void getAllCategoryHeadsWithSharedHead() {
        // prepare
        final var testCategoryName1 = "CategoryName1";
        final var testCategoryName2 = "CategoryName2";
        val testCategoryHeadShared = Head.builder().name("CategoryHeadShared").build();

        when(categorizable.getCategoryNames())
                .thenReturn(List.of(testCategoryName1, testCategoryName2));

        when(categorizable.getCategoryHeads(testCategoryName1))
                .thenReturn(List.of(testCategoryHeadShared));
        when(categorizable.getCategoryHeads(testCategoryName2))
                .thenReturn(List.of(testCategoryHeadShared));

        // execute
        val actual = categorizable.getAllCategoryHeads();

        // verify
        assertThat(actual, Matchers.contains(testCategoryHeadShared));
    }
}

class DummyCategorizable implements Categorizable {

    @Override
    public List<Head> getCategoryHeads(@NonNull String categoryName) {
        return null;
    }

    @Override
    public List<String> getCategoryNames() {
        return null;
    }

    @Override
    public String getSource() {
        return null;
    }
}