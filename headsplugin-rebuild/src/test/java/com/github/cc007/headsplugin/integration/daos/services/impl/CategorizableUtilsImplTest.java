package com.github.cc007.headsplugin.integration.daos.services.impl;

import com.github.cc007.headsplugin.integration.daos.interfaces.Categorizable;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategorizableUtilsImplTest {

    @Mock
    List<Categorizable> categorizables;

    @InjectMocks
    CategorizableUtilsImpl categorizableUtils;

    @Test
    void getCategoryMap() {
        // prepare
        val testCategoryName1 = "CategoryName1";
        val testCategoryName2 = "CategoryName2";
        val testCategorizable = Mockito.mock(Categorizable.class);

        when(categorizables.iterator())
                .thenReturn(Collections.singletonList(testCategorizable).iterator());

        when(testCategorizable.getCategoryNames())
                .thenReturn(Arrays.asList(testCategoryName1, testCategoryName2));

        // execute
        val actual1 = categorizableUtils.getCategoryMap();
        val actual2 = categorizableUtils.getCategoryMap();

        // verify
        assertThat(actual1, is(sameInstance(actual2)));

        assertThat(actual1.keySet(), containsInAnyOrder(testCategoryName1, testCategoryName2));
        assertThat(actual1.get(testCategoryName1), contains(testCategorizable));
        assertThat(actual1.get(testCategoryName2), contains(testCategorizable));
    }

    @Test
    void getCategoryMapWithClear() {
        // prepare
        val testCategoryName1 = "CategoryName1";
        val testCategoryName2 = "CategoryName2";
        val testCategoryName3 = "CategoryName3";
        val testCategorizable = Mockito.mock(Categorizable.class);

        //noinspection unchecked
        when(categorizables.iterator())
                .thenReturn(
                        Collections.singletonList(testCategorizable).iterator(),
                        Collections.singletonList(testCategorizable).iterator()
                );

        //noinspection unchecked
        when(testCategorizable.getCategoryNames())
                .thenReturn(
                        Arrays.asList(testCategoryName1, testCategoryName2),
                        Arrays.asList(testCategoryName2, testCategoryName3)
                );

        // execute
        val actual1 = categorizableUtils.getCategoryMap();
        categorizableUtils.clearCategoryMap();
        val actual2 = categorizableUtils.getCategoryMap();

        // verify
        assertThat(actual1, is(not(sameInstance(actual2))));

        assertThat(actual1.keySet(), containsInAnyOrder(testCategoryName1, testCategoryName2));
        assertThat(actual1.get(testCategoryName1), contains(testCategorizable));
        assertThat(actual1.get(testCategoryName2), contains(testCategorizable));

        assertThat(actual2.keySet(), containsInAnyOrder(testCategoryName2, testCategoryName3));
        assertThat(actual2.get(testCategoryName2), contains(testCategorizable));
        assertThat(actual2.get(testCategoryName3), contains(testCategorizable));
    }

    @Test
    void getCategoryMapWithCommonCategoryName() {
        // prepare
        val testCategoryName1 = "CategoryName1";
        val testCategoryName2 = "CategoryName2";
        val testCategoryName3 = "CategoryName3";
        val testCategorizable1 = Mockito.mock(Categorizable.class);
        val testCategorizable2 = Mockito.mock(Categorizable.class);

        when(categorizables.iterator())
                .thenReturn(Arrays.asList(testCategorizable1, testCategorizable2).iterator());

        when(testCategorizable1.getCategoryNames())
                .thenReturn(Arrays.asList(testCategoryName1, testCategoryName2));
        when(testCategorizable2.getCategoryNames())
                .thenReturn(Arrays.asList(testCategoryName2, testCategoryName3));

        // execute
        val actual = categorizableUtils.getCategoryMap();

        // verify
        assertThat(actual.keySet(), containsInAnyOrder(testCategoryName1, testCategoryName2, testCategoryName3));
        assertThat(actual.get(testCategoryName1), contains(testCategorizable1));
        assertThat(actual.get(testCategoryName2), containsInAnyOrder(testCategorizable1, testCategorizable2));
        assertThat(actual.get(testCategoryName3), contains(testCategorizable2));
    }
}