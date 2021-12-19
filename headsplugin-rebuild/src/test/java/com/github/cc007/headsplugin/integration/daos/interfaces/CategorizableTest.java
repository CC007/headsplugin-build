package com.github.cc007.headsplugin.integration.daos.interfaces;

import com.github.cc007.headsplugin.api.business.domain.Head;

import lombok.NonNull;
import lombok.val;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
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
        val testCategoryName1 = "CategoryName1";
        val testCategoryName2 = "CategoryName2";
        val testCategoryName3 = "CategoryName3";
        val testCategoryHead11 = Head.builder().name("CategoryHead11").build();
        val testCategoryHead12 = Head.builder().name("CategoryHead12").build();
        val testCategoryHead31 = Head.builder().name("CategoryHead31").build();

        when(categorizable.getCategoryNames())
                .thenReturn(Arrays.asList(testCategoryName1, testCategoryName2, testCategoryName3));

        when(categorizable.getCategoryHeads(testCategoryName1))
                .thenReturn(Arrays.asList(testCategoryHead11, testCategoryHead12));
        when(categorizable.getCategoryHeads(testCategoryName2))
                .thenReturn(Collections.emptyList());
        when(categorizable.getCategoryHeads(testCategoryName3))
                .thenReturn(Collections.singletonList(testCategoryHead31));

        // execute
        val actual = categorizable.getAllCategoryHeads();

        // verify
        assertThat(actual, Matchers.containsInAnyOrder(testCategoryHead11, testCategoryHead12, testCategoryHead31));
    }

    @Test
    void getAllCategoryHeadsWithSharedHead() {
        // prepare
        val testCategoryName1 = "CategoryName1";
        val testCategoryName2 = "CategoryName2";
        val testCategoryHeadShared = Head.builder().name("CategoryHeadShared").build();

        when(categorizable.getCategoryNames())
                .thenReturn(Arrays.asList(testCategoryName1, testCategoryName2));

        when(categorizable.getCategoryHeads(testCategoryName1))
                .thenReturn(Collections.singletonList(testCategoryHeadShared));
        when(categorizable.getCategoryHeads(testCategoryName2))
                .thenReturn(Collections.singletonList(testCategoryHeadShared));

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