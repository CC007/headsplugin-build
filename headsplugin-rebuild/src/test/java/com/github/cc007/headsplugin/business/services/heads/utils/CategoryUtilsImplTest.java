package com.github.cc007.headsplugin.business.services.heads.utils;

import com.github.cc007.headsplugin.integration.daos.interfaces.Categorizable;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryUtilsImplTest {

    @Mock
    Set<Categorizable> categorizables;

    @InjectMocks
    CategoryUtilsImpl categorizableUtils;

    @Test
    void getCategoryMap() {
        // prepare
        final var testCategoryName1 = "CategoryName1";
        final var testCategoryName2 = "CategoryName2";
        final var testCategorizable = Mockito.mock(Categorizable.class);

        when(categorizables.iterator())
                .thenReturn(List.of(testCategorizable).iterator());

        when(testCategorizable.getCategoryNames())
                .thenReturn(List.of(testCategoryName1, testCategoryName2));

        // execute
        final var actual1 = categorizableUtils.getCategoryMap();
        final var actual2 = categorizableUtils.getCategoryMap();

        // verify
        assertThat(actual1, is(sameInstance(actual2)));

        assertThat(actual1.keySet(), containsInAnyOrder(testCategoryName1, testCategoryName2));
        assertThat(actual1.get(testCategoryName1), contains(testCategorizable));
        assertThat(actual1.get(testCategoryName2), contains(testCategorizable));
    }

    @Test
    void getCategoryMapWithClear() {
        // prepare
        final var testCategoryName1 = "CategoryName1";
        final var testCategoryName2 = "CategoryName2";
        final var testCategoryName3 = "CategoryName3";
        final var testCategorizable = Mockito.mock(Categorizable.class);

        //noinspection unchecked
        when(categorizables.iterator())
                .thenReturn(
                        List.of(testCategorizable).iterator(),
                        List.of(testCategorizable).iterator()
                );

        //noinspection unchecked
        when(testCategorizable.getCategoryNames())
                .thenReturn(
                        List.of(testCategoryName1, testCategoryName2),
                        List.of(testCategoryName2, testCategoryName3)
                );

        // execute
        final var actual1 = categorizableUtils.getCategoryMap();
        categorizableUtils.clearCategoryMap();
        final var actual2 = categorizableUtils.getCategoryMap();

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
        final var testCategoryName1 = "CategoryName1";
        final var testCategoryName2 = "CategoryName2";
        final var testCategoryName3 = "CategoryName3";
        final var testCategorizable1 = Mockito.mock(Categorizable.class);
        final var testCategorizable2 = Mockito.mock(Categorizable.class);

        when(categorizables.iterator())
                .thenReturn(List.of(testCategorizable1, testCategorizable2).iterator());

        when(testCategorizable1.getCategoryNames())
                .thenReturn(List.of(testCategoryName1, testCategoryName2));
        when(testCategorizable2.getCategoryNames())
                .thenReturn(List.of(testCategoryName2, testCategoryName3));

        // execute
        final var actual = categorizableUtils.getCategoryMap();

        // verify
        assertThat(actual.keySet(), containsInAnyOrder(testCategoryName1, testCategoryName2, testCategoryName3));
        assertThat(actual.get(testCategoryName1), contains(testCategorizable1));
        assertThat(actual.get(testCategoryName2), containsInAnyOrder(testCategorizable1, testCategorizable2));
        assertThat(actual.get(testCategoryName3), contains(testCategorizable2));
    }
}