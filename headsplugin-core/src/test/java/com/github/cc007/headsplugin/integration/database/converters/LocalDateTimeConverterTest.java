package com.github.cc007.headsplugin.integration.database.converters;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.is;

class LocalDateTimeConverterTest {

    LocalDateTimeConverter localDateTimeConverter = new LocalDateTimeConverter();

    @Test
    void convertToDatabaseColumn() {
        // prepare
        LocalDateTime expected = LocalDateTime.now();

        // execute
        Timestamp actual = localDateTimeConverter.convertToDatabaseColumn(expected);

        // verify
        MatcherAssert.assertThat(actual.getYear() + 1900, is(expected.getYear()));
        MatcherAssert.assertThat(actual.getMonth() + 1, is(expected.getMonthValue()));
        MatcherAssert.assertThat(actual.getDate(), is(expected.getDayOfMonth()));
        MatcherAssert.assertThat(actual.getHours(), is(expected.getHour()));
        MatcherAssert.assertThat(actual.getMinutes(), is(expected.getMinute()));
        MatcherAssert.assertThat(actual.getSeconds(), is(expected.getSecond()));
    }

    @Test
    void convertToEntityAttribute() {
        // prepare
        Timestamp expected = new Timestamp(System.currentTimeMillis());

        // execute
        LocalDateTime actual = localDateTimeConverter.convertToEntityAttribute(expected);

        // verify
        MatcherAssert.assertThat(actual.getYear(), is(expected.getYear() + 1900));
        MatcherAssert.assertThat(actual.getMonthValue(), is(expected.getMonth() + 1));
        MatcherAssert.assertThat(actual.getDayOfMonth(), is(expected.getDate()));
        MatcherAssert.assertThat(actual.getHour(), is(expected.getHours()));
        MatcherAssert.assertThat(actual.getMinute(), is(expected.getMinutes()));
        MatcherAssert.assertThat(actual.getSecond(), is(expected.getSeconds()));
    }
}