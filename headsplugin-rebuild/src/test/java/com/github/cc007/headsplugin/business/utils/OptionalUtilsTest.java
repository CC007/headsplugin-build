package com.github.cc007.headsplugin.business.utils;

import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static com.github.npathai.hamcrestopt.OptionalMatchers.isEmpty;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresentAndIs;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

class OptionalUtilsTest {

    @Test
    void peekWhenPresent() {
        // prepare
        final var testString = "TestString";
        final var testOptionalString = Optional.of(testString);
        final var result = new AtomicReference<String>();
        final var testConsumer = (Consumer<String>) result::set;

        // execute
        final var actual = OptionalUtils.peek(testOptionalString, testConsumer);

        // verify
        assertThat(actual, isPresentAndIs(testString));
        assertThat(result.get(), is(testString));
    }

    @Test
    void peekWhenEmpty() {
        // prepare
        final var testOptionalString = Optional.<String>empty();
        final var result = new AtomicReference<String>();
        final var testConsumer = (Consumer<String>) result::set;

        // execute
        final var actual = OptionalUtils.peek(testOptionalString, testConsumer);

        // verify
        assertThat(actual, isEmpty());
        assertThat(result.get(), nullValue());
    }

    @Test
    void peekOptionalNull() {
        // prepare
        final var result = new AtomicReference<String>();
        val testConsumer = (Consumer<String>) result::set;

        // execute
        val actualException = Assertions.assertThrows(NullPointerException.class, () ->
                OptionalUtils.peek(null, testConsumer)
        );

        // verify
        assertThat(actualException.getMessage(), containsString("is marked non-null but is null"));
        assertThat(result.get(), nullValue());
    }

    @Test
    void peekConsumerNull() {
        // prepare
        val testOptionalString = Optional.<String>empty();
        val result = new AtomicReference<String>();

        // execute
        val actualException = Assertions.assertThrows(NullPointerException.class, () ->
                OptionalUtils.peek(testOptionalString, null)
        );

        // verify
        assertThat(actualException.getMessage(), containsString("is marked non-null but is null"));
        assertThat(result.get(), nullValue());
    }
}