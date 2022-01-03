package com.github.cc007.headsplugin.business.utils;

import lombok.val;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static com.github.npathai.hamcrestopt.OptionalMatchers.isEmpty;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresentAndIs;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

class OptionalUtilsTest {

    @Test
    void peekWhenPresent() {
        // prepare
        val testString = "TestString";
        val testOptionalString = Optional.of(testString);
        val result = new AtomicReference<String>();
        val testConsumer = (Consumer<String>) result::set;

        // execute
        val actual = OptionalUtils.peek(testOptionalString, testConsumer);

        // verify
        assertThat(actual, isPresentAndIs(testString));
        assertThat(result.get(), is(testString));
    }

    @Test
    void peekWhenEmpty() {
        // prepare
        val testOptionalString = Optional.<String>empty();
        val result = new AtomicReference<String>();
        val testConsumer = (Consumer<String>) result::set;

        // execute
        val actual = OptionalUtils.peek(testOptionalString, testConsumer);

        // verify
        assertThat(actual, isEmpty());
        assertThat(result.get(), nullValue());
    }
}