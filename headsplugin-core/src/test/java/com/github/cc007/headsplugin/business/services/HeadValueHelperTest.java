package com.github.cc007.headsplugin.business.services;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.github.npathai.hamcrestopt.OptionalMatchers.isEmpty;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresentAnd;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.matchesPattern;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HeadValueHelperTest {

    HeadValueHelper headValueHelper = new HeadValueHelper();

    @Mock(lenient = true)
    private Appender appenderMock;

    private Appender realAppender;

    @Captor
    private ArgumentCaptor<LogEvent> captorLoggingEvent;

    private Logger logger;

    @BeforeEach
    void setUp() {
        when(appenderMock.getName()).thenReturn("MockAppender");
        when(appenderMock.isStarted()).thenReturn(true);

        logger = (Logger) LogManager.getLogger(HeadValueHelper.class);
        logger.addAppender(appenderMock);
        logger.setLevel(Level.DEBUG);

        realAppender = logger.getParent().getAppenders().get("DefaultConsole-2");
        logger.getParent().removeAppender(realAppender);
    }

    @AfterEach
    void tearDown() {
        logger.removeAppender(appenderMock);
        logger.getParent().addAppender(realAppender);
    }

    /**
     * Test if a warning was logged, using the base64 encoded version of <code>bla</code>.
     * <code>{"textures":{"SKIN":{"url":"http://textures.minecraft.net/texture/74e9c6e98582ffd8ff8feb3322cd1849c43fb16b158abb11ca7b42eda7743eb"}}}</code>
     */
    @Test
    void parseHeadValue() {
        // prepare
        final var headValue = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzRlOWM2ZTk4NTgyZmZkOGZmOGZlYjMzMjJjZDE4NDljNDNmYjE2YjE1OGFiYjExY2E3YjQyZWRhNzc0M2ViIn19fQ";
        final var skinUrl = "http://textures.minecraft.net/texture/74e9c6e98582ffd8ff8feb3322cd1849c43fb16b158abb11ca7b42eda7743eb";

        // execute
        final var actual = headValueHelper.parseHeadValue(headValue);

        // verify
        assertThat(actual, isPresentAnd(hasToString(skinUrl)));
    }

    /**
     * Test if a warning was logged for using an invalid url,
     * using the base64 encoded version of <code>{"textures":{"SKIN":{"url":"not-a-url"}}}</code>
     */
    @Test
    void parseHeadValueUrlInvalid() {
        // prepare
        final var headValue = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Im5vdC1hLXVybCJ9fX0=";
        final var urlString = "not-a-url";

        // execute
        final var actual = headValueHelper.parseHeadValue(headValue);

        // verify
        verify(appenderMock).append(captorLoggingEvent.capture());
        final var logEvents = captorLoggingEvent.getAllValues();
        assertThat(logEvents, hasSize(1));
        assertThat(logEvents.get(0).getMessage().getFormattedMessage(), is("Unable to parse \"" + urlString + "\" as URL: no protocol: " + urlString));
        assertThat(actual, isEmpty());
    }

    /**
     * Test if a warning was logged for not providing a string with the url key,
     * using the base64 encoded version of <code>{"textures":{"SKIN":{"url":{}}}}</code>
     */
    @Test
    void parseHeadValueUrlKeyNotString() {
        // prepare
        final var headValue = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6e319fX0=";

        // execute
        final var actual = headValueHelper.parseHeadValue(headValue);

        // verify
        verify(appenderMock).append(captorLoggingEvent.capture());
        final var logEvents = captorLoggingEvent.getAllValues();
        assertThat(logEvents, hasSize(1));
        assertThat(logEvents.get(0).getMessage().getFormattedMessage(), is("Key \"url\" doesn't contain a JSON primitive: {}"));
        assertThat(actual, isEmpty());
    }

    /**
     * Test if a warning was logged for using the wrong key,
     * using the base64 encoded version of <code>{"textures":{"SKIN":{"urls":""}}}</code>
     */
    @Test
    void parseHeadValueUrlKeyTypo() {
        // prepare
        final var headValue = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybHMiOiIifX19";

        // execute
        final var actual = headValueHelper.parseHeadValue(headValue);

        // verify
        verify(appenderMock).append(captorLoggingEvent.capture());
        final var logEvents = captorLoggingEvent.getAllValues();
        assertThat(logEvents, hasSize(1));
        assertThat(logEvents.get(0).getMessage().getFormattedMessage(), is("Key \"url\" not found"));
        assertThat(actual, isEmpty());
    }

    /**
     * Test if a warning was logged for not providing a string with the url key,
     * using the base64 encoded version of <code>{"textures":{"SKIN":""}}</code>
     */
    @Test
    void parseHeadValueSKINKeyNotJsonObject() {
        // prepare
        final var headValue = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjoiIn19";

        // execute
        final var actual = headValueHelper.parseHeadValue(headValue);

        // verify
        verify(appenderMock).append(captorLoggingEvent.capture());
        final var logEvents = captorLoggingEvent.getAllValues();
        assertThat(logEvents, hasSize(1));
        assertThat(logEvents.get(0).getMessage().getFormattedMessage(), is("Key \"SKIN\" doesn't contain a JSON object: \"\""));
        assertThat(actual, isEmpty());
    }

    /**
     * Test if a warning was logged for using the wrong key,
     * using the base64 encoded version of <code>{"textures":{"SKINS":{}}}</code>
     */
    @Test
    void parseHeadValueSKINKeyTypo() {
        // prepare
        final var headValue = "eyJ0ZXh0dXJlcyI6eyJTS0lOUyI6e319fQ==";

        // execute
        final var actual = headValueHelper.parseHeadValue(headValue);

        // verify
        verify(appenderMock).append(captorLoggingEvent.capture());
        final var logEvents = captorLoggingEvent.getAllValues();
        assertThat(logEvents, hasSize(1));
        assertThat(logEvents.get(0).getMessage().getFormattedMessage(), is("Key \"SKIN\" not found"));
        assertThat(actual, isEmpty());
    }

    /**
     * Test if a warning was logged for not providing a string with the url key,
     * using the base64 encoded version of <code>{"textures":""}</code>
     */
    @Test
    void parseHeadValueTexturesKeyNotJsonObject() {
        // prepare
        final var headValue = "eyJ0ZXh0dXJlcyI6IiJ9";

        // execute
        final var actual = headValueHelper.parseHeadValue(headValue);

        // verify
        verify(appenderMock).append(captorLoggingEvent.capture());
        final var logEvents = captorLoggingEvent.getAllValues();
        assertThat(logEvents, hasSize(1));
        assertThat(logEvents.get(0).getMessage().getFormattedMessage(), is("Key \"textures\" doesn't contain a JSON object: \"\""));
        assertThat(actual, isEmpty());
    }

    /**
     * Test if a warning was logged for using the wrong key,
     * using the base64 encoded version of <code>{"texture":{}}</code>
     */
    @Test
    void parseHeadValueTexturesKeyTypo() {
        // prepare
        final var headValue = "eyJ0ZXh0dXJlIjp7fX0=";

        // execute
        final var actual = headValueHelper.parseHeadValue(headValue);

        // verify
        verify(appenderMock).append(captorLoggingEvent.capture());
        final var logEvents = captorLoggingEvent.getAllValues();
        assertThat(logEvents, hasSize(1));
        assertThat(logEvents.get(0).getMessage().getFormattedMessage(), is("Key \"textures\" not found"));
        assertThat(actual, isEmpty());
    }

    /**
     * Test if a warning was logged for not supplying a json object,
     * using the base64 encoded version of <code>bla</code>.
     */
    @Test
    void parseHeadValueNotJsonObject() {
        // prepare
        final var headValue = "Ymxh";
        final var decodedHeadValue = "bla";

        // execute
        final var actual = headValueHelper.parseHeadValue(headValue);

        // verify
        verify(appenderMock).append(captorLoggingEvent.capture());
        final var logEvents = captorLoggingEvent.getAllValues();
        assertThat(logEvents, hasSize(1));
        assertThat(logEvents.get(0).getMessage().getFormattedMessage(), is("Root element doesn't contain a JSON object: \"" + decodedHeadValue + "\""));
        assertThat(actual, isEmpty());
    }

    /**
     * Test if a warning was logged for providing an invalid json string,
     * using the base64 encoded version of <code>}bla</code>.
     * <p>
     * Normally this would trigger a MalformedJsonException, but this should be catched and a warning should be thrown instead
     */
    @Test
    void parseHeadValueNotJson() {
        // prepare
        final var headValue = "fWJsYQ==";
        final var decodedHeadValue = "}bla";

        // execute
        final var actual = headValueHelper.parseHeadValue(headValue);

        // verify
        verify(appenderMock).append(captorLoggingEvent.capture());
        final var logEvents = captorLoggingEvent.getAllValues();
        assertThat(logEvents, hasSize(1));
        final var message = logEvents.get(0).getMessage().getFormattedMessage();
        assertThat(message, matchesPattern("Unable to parse \"" + decodedHeadValue + "\" as JSON: com.google.gson.stream.MalformedJsonException: .*"));
        assertThat(actual, isEmpty());
    }

    /**
     * Test if a warning was logged for using illegal characters in a base64 string,
     * using <code>;bla</code> as the input
     */
    @Test
    void parseHeadValueNotBase64() {
        // prepare
        final var headValue = ";bla";

        // execute
        final var actual = headValueHelper.parseHeadValue(headValue);

        // verify
        verify(appenderMock).append(captorLoggingEvent.capture());
        final var logEvents = captorLoggingEvent.getAllValues();
        assertThat(logEvents, hasSize(1));
        assertThat(logEvents.get(0).getMessage().getFormattedMessage(), is("Unable to base64 decode \"" + headValue + "\": Illegal base64 character 3b"));
        assertThat(actual, isEmpty());
    }
}