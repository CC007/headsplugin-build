package com.github.cc007.headsplugin.business.services;

import com.github.cc007.headsplugin.api.business.domain.Head;
import com.github.cc007.headsplugin.business.model.McVersion;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.bukkit.Bukkit;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URL;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.matchesPattern;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OwnerProfileServiceTest {


    @Mock(lenient = true)
    private Appender appenderMock;

    private Appender realAppender;

    @Mock
    private McVersion mcVersion;

    @InjectMocks
    OwnerProfileService ownerProfileService;

    @Captor
    private ArgumentCaptor<LogEvent> captorLoggingEvent;

    @Captor
    private ArgumentCaptor<URL> captorUrl;

    private Logger logger;

    @BeforeEach
    void setUp() {
        when(appenderMock.getName()).thenReturn("MockAppender");
        when(appenderMock.isStarted()).thenReturn(true);

        logger = (Logger) LogManager.getLogger(OwnerProfileService.class);
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
     * Test if a normal OwnerProfile was created with {@link Bukkit#createPlayerProfile(UUID, String)},
     * even with a long name or with special characters, so long as the version is lower than 1.21.
     * <p>
     * using the base64 encoded version of <code>{"textures":{"SKIN":{"url":"http://textures.minecraft.net/texture/74e9c6e98582ffd8ff8feb3322cd1849c43fb16b158abb11ca7b42eda7743eb"}}}</code>
     */
    @ParameterizedTest
    @ValueSource(strings = {
            "testName",
            "test!name",
            "aVeryVeryLongTestName",
            "test name with spaces",
            "tést\tnÃme",
    })
    void createOwnerProfileMinorVersion20(String headName) {
        // prepare
        try (
                MockedStatic<Bukkit> bukkitMock = Mockito.mockStatic(Bukkit.class);
        ) {
            final var headHeadOwner = UUID.randomUUID();
            final var headValue = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzRlOWM2ZTk4NTgyZmZkOGZmOGZlYjMzMjJjZDE4NDljNDNmYjE2YjE1OGFiYjExY2E3YjQyZWRhNzc0M2ViIn19fQ";
            final var skinUrl = "http://textures.minecraft.net/texture/74e9c6e98582ffd8ff8feb3322cd1849c43fb16b158abb11ca7b42eda7743eb";

            final var head = Head.builder()
                    .name(headName)
                    .headOwner(headHeadOwner)
                    .value(headValue)
                    .build();

            final var ownerProfileMock = mock(PlayerProfile.class);
            final var ownerTexturesMock = mock(PlayerTextures.class);

            doReturn(20)
                    .when(mcVersion).minor();
            doReturn(2)
                    .when(mcVersion).patch();
            bukkitMock.when(() -> Bukkit.createPlayerProfile(headHeadOwner, headName))
                    .thenReturn(ownerProfileMock);
            doReturn(ownerTexturesMock)
                    .when(ownerProfileMock).getTextures();

            // execute
            final var actual = ownerProfileService.createOwnerProfile(head);

            // verify
            verify(ownerTexturesMock).setSkin(captorUrl.capture());
            verifyNoMoreInteractions(ownerProfileMock, ownerTexturesMock);
            assertThat(actual, is(ownerProfileMock));
            assertThat(captorUrl.getValue(), hasToString(skinUrl));
        }
    }

    /**
     * Test if a normal OwnerProfile was created with {@link Bukkit#createPlayerProfile(UUID, String)},
     * even with a long name when the version is 1.21.
     * The name should now be truncated to 16 characters.
     * <p>
     * using the base64 encoded version of <code>{"textures":{"SKIN":{"url":"http://textures.minecraft.net/texture/74e9c6e98582ffd8ff8feb3322cd1849c43fb16b158abb11ca7b42eda7743eb"}}}</code>
     */
    @ParameterizedTest
    @CsvSource({
            "testName, testName",
            "test!name, test!name",
            "aVeryVeryLongTestName, aVeryVeryLongTes",
            "test name with spaces, test_name_with_s",
            "tést\tnÃme, t*st_n*me",
    })
    void createOwnerProfileMinorVersion21(String headName, String fixedHeadName) {
        // prepare
        try (
                MockedStatic<Bukkit> bukkitMock = Mockito.mockStatic(Bukkit.class);
        ) {
            final var headHeadOwner = UUID.randomUUID();
            final var headValue = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzRlOWM2ZTk4NTgyZmZkOGZmOGZlYjMzMjJjZDE4NDljNDNmYjE2YjE1OGFiYjExY2E3YjQyZWRhNzc0M2ViIn19fQ";
            final var skinUrl = "http://textures.minecraft.net/texture/74e9c6e98582ffd8ff8feb3322cd1849c43fb16b158abb11ca7b42eda7743eb";

            final var head = Head.builder()
                    .name(headName)
                    .headOwner(headHeadOwner)
                    .value(headValue)
                    .build();

            final var ownerProfileMock = mock(PlayerProfile.class);
            final var ownerTexturesMock = mock(PlayerTextures.class);

            doReturn(21)
                    .when(mcVersion).minor();
            bukkitMock.when(() -> Bukkit.createPlayerProfile(headHeadOwner, fixedHeadName))
                    .thenReturn(ownerProfileMock);
            doReturn(ownerTexturesMock)
                    .when(ownerProfileMock).getTextures();

            // execute
            final var actual = ownerProfileService.createOwnerProfile(head);

            // verify
            verify(ownerTexturesMock).setSkin(captorUrl.capture());
            verifyNoMoreInteractions(ownerProfileMock, ownerTexturesMock);
            assertThat(actual, is(ownerProfileMock));
            assertThat(captorUrl.getValue(), hasToString(skinUrl));
        }
    }

    /**
     * Test if a normal OwnerProfile was created with {@link Bukkit#createPlayerProfile(UUID, String)},
     * even with a long name when the version is 1.20.6.
     * The name should now be truncated to 16 characters.
     * <p>
     * using the base64 encoded version of <code>{"textures":{"SKIN":{"url":"http://textures.minecraft.net/texture/74e9c6e98582ffd8ff8feb3322cd1849c43fb16b158abb11ca7b42eda7743eb"}}}</code>
     */
    @ParameterizedTest
    @CsvSource({
            "testName, testName",
            "test!name, test!name",
            "aVeryVeryLongTestName, aVeryVeryLongTes",
            "test name with spaces, test_name_with_s",
            "tést\tnÃme, t*st_n*me",
    })
    void createOwnerProfileMinorVersion20Dot6(String headName, String fixedHeadName) {
        // prepare
        try (
                MockedStatic<Bukkit> bukkitMock = Mockito.mockStatic(Bukkit.class);
        ) {
            final var headHeadOwner = UUID.randomUUID();
            final var headValue = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzRlOWM2ZTk4NTgyZmZkOGZmOGZlYjMzMjJjZDE4NDljNDNmYjE2YjE1OGFiYjExY2E3YjQyZWRhNzc0M2ViIn19fQ";
            final var skinUrl = "http://textures.minecraft.net/texture/74e9c6e98582ffd8ff8feb3322cd1849c43fb16b158abb11ca7b42eda7743eb";

            final var head = Head.builder()
                    .name(headName)
                    .headOwner(headHeadOwner)
                    .value(headValue)
                    .build();

            final var ownerProfileMock = mock(PlayerProfile.class);
            final var ownerTexturesMock = mock(PlayerTextures.class);

            doReturn(20)
                    .when(mcVersion).minor();
            doReturn(6)
                    .when(mcVersion).patch();
            bukkitMock.when(() -> Bukkit.createPlayerProfile(headHeadOwner, fixedHeadName))
                    .thenReturn(ownerProfileMock);
            doReturn(ownerTexturesMock)
                    .when(ownerProfileMock).getTextures();

            // execute
            final var actual = ownerProfileService.createOwnerProfile(head);

            // verify
            verify(ownerTexturesMock).setSkin(captorUrl.capture());
            verifyNoMoreInteractions(ownerProfileMock, ownerTexturesMock);
            assertThat(actual, is(ownerProfileMock));
            assertThat(captorUrl.getValue(), hasToString(skinUrl));
        }
    }

    /**
     * Test if a warning was logged for using an invalid url,
     * using the base64 encoded version of <code>{"textures":{"SKIN":{"url":"not-a-url"}}}</code>
     */
    @Test
    void createOwnerProfileUrlInvalid() {
        // prepare
        try (
                MockedStatic<Bukkit> bukkitMock = Mockito.mockStatic(Bukkit.class);
        ) {
            final var headName = "testName";
            final var headHeadOwner = UUID.randomUUID();
            final var headValue = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Im5vdC1hLXVybCJ9fX0=";
            final var urlString = "not-a-url";

            final var head = Head.builder()
                    .name(headName)
                    .headOwner(headHeadOwner)
                    .value(headValue)
                    .build();

            final var ownerProfileMock = mock(PlayerProfile.class);

            doReturn(20)
                    .when(mcVersion).minor();
            bukkitMock.when(() -> Bukkit.createPlayerProfile(headHeadOwner, headName))
                    .thenReturn(ownerProfileMock);

            // execute
            final var actual = ownerProfileService.createOwnerProfile(head);

            // verify

            verify(appenderMock).append(captorLoggingEvent.capture());
            final var logEvents = captorLoggingEvent.getAllValues();
            assertThat(logEvents, hasSize(1));
            assertThat(logEvents.get(0).getMessage().getFormattedMessage(), is("Unable to parse \"" + urlString + "\" as URL: no protocol: " + urlString));

            verifyNoInteractions(ownerProfileMock);
            assertThat(actual, is(ownerProfileMock));
        }
    }

    /**
     * Test if a warning was logged for not providing a string with the url key,
     * using the base64 encoded version of <code>{"textures":{"SKIN":{"url":{}}}}</code>
     */
    @Test
    void createOwnerProfileUrlKeyNotString() {
        // prepare
        try (
                MockedStatic<Bukkit> bukkitMock = Mockito.mockStatic(Bukkit.class);
        ) {
            final var headName = "testName";
            final var headHeadOwner = UUID.randomUUID();
            final var headValue = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6e319fX0=";

            final var head = Head.builder()
                    .name(headName)
                    .headOwner(headHeadOwner)
                    .value(headValue)
                    .build();

            final var ownerProfileMock = mock(PlayerProfile.class);

            doReturn(20)
                    .when(mcVersion).minor();
            bukkitMock.when(() -> Bukkit.createPlayerProfile(headHeadOwner, headName))
                    .thenReturn(ownerProfileMock);

            // execute
            final var actual = ownerProfileService.createOwnerProfile(head);

            // verify
            verify(appenderMock).append(captorLoggingEvent.capture());
            final var logEvents = captorLoggingEvent.getAllValues();
            assertThat(logEvents, hasSize(1));
            assertThat(logEvents.get(0).getMessage().getFormattedMessage(), is("Key \"url\" doesn't contain a JSON primitive: {}"));

            verifyNoInteractions(ownerProfileMock);
            assertThat(actual, is(ownerProfileMock));
        }
    }

    /**
     * Test if a warning was logged for using the wrong key,
     * using the base64 encoded version of <code>{"textures":{"SKIN":{"urls":""}}}</code>
     */
    @Test
    void createOwnerProfileUrlKeyTypo() {
        // prepare
        try (
                MockedStatic<Bukkit> bukkitMock = Mockito.mockStatic(Bukkit.class);
        ) {
            final var headName = "testName";
            final var headHeadOwner = UUID.randomUUID();
            final var headValue = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybHMiOiIifX19";

            final var head = Head.builder()
                    .name(headName)
                    .headOwner(headHeadOwner)
                    .value(headValue)
                    .build();

            final var ownerProfileMock = mock(PlayerProfile.class);

            doReturn(20)
                    .when(mcVersion).minor();
            bukkitMock.when(() -> Bukkit.createPlayerProfile(headHeadOwner, headName))
                    .thenReturn(ownerProfileMock);

            // execute
            final var actual = ownerProfileService.createOwnerProfile(head);

            // verify
            verify(appenderMock).append(captorLoggingEvent.capture());
            final var logEvents = captorLoggingEvent.getAllValues();
            assertThat(logEvents, hasSize(1));
            assertThat(logEvents.get(0).getMessage().getFormattedMessage(), is("Key \"url\" not found"));

            verifyNoInteractions(ownerProfileMock);
            assertThat(actual, is(ownerProfileMock));
        }
    }

    /**
     * Test if a warning was logged for not providing a string with the url key,
     * using the base64 encoded version of <code>{"textures":{"SKIN":""}}</code>
     */
    @Test
    void createOwnerProfileSKINKeyNotJsonObject() {
        // prepare
        try (
                MockedStatic<Bukkit> bukkitMock = Mockito.mockStatic(Bukkit.class);
        ) {
            final var headName = "testName";
            final var headHeadOwner = UUID.randomUUID();
            final var headValue = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjoiIn19";

            final var head = Head.builder()
                    .name(headName)
                    .headOwner(headHeadOwner)
                    .value(headValue)
                    .build();

            final var ownerProfileMock = mock(PlayerProfile.class);

            doReturn(20)
                    .when(mcVersion).minor();
            bukkitMock.when(() -> Bukkit.createPlayerProfile(headHeadOwner, headName))
                    .thenReturn(ownerProfileMock);

            // execute
            final var actual = ownerProfileService.createOwnerProfile(head);

            // verify
            verify(appenderMock).append(captorLoggingEvent.capture());
            final var logEvents = captorLoggingEvent.getAllValues();
            assertThat(logEvents, hasSize(1));
            assertThat(logEvents.get(0).getMessage().getFormattedMessage(), is("Key \"SKIN\" doesn't contain a JSON object: \"\""));

            verifyNoInteractions(ownerProfileMock);
            assertThat(actual, is(ownerProfileMock));
        }
    }

    /**
     * Test if a warning was logged for using the wrong key,
     * using the base64 encoded version of <code>{"textures":{"SKINS":{}}}</code>
     */
    @Test
    void createOwnerProfileSKINKeyTypo() {
        // prepare
        try (
                MockedStatic<Bukkit> bukkitMock = Mockito.mockStatic(Bukkit.class);
        ) {
            final var headName = "testName";
            final var headHeadOwner = UUID.randomUUID();
            final var headValue = "eyJ0ZXh0dXJlcyI6eyJTS0lOUyI6e319fQ==";

            final var head = Head.builder()
                    .name(headName)
                    .headOwner(headHeadOwner)
                    .value(headValue)
                    .build();

            final var ownerProfileMock = mock(PlayerProfile.class);

            doReturn(20)
                    .when(mcVersion).minor();
            bukkitMock.when(() -> Bukkit.createPlayerProfile(headHeadOwner, headName))
                    .thenReturn(ownerProfileMock);

            // execute
            final var actual = ownerProfileService.createOwnerProfile(head);

            // verify
            verify(appenderMock).append(captorLoggingEvent.capture());
            final var logEvents = captorLoggingEvent.getAllValues();
            assertThat(logEvents, hasSize(1));
            assertThat(logEvents.get(0).getMessage().getFormattedMessage(), is("Key \"SKIN\" not found"));

            verifyNoInteractions(ownerProfileMock);
            assertThat(actual, is(ownerProfileMock));
        }
    }

    /**
     * Test if a warning was logged for not providing a string with the url key,
     * using the base64 encoded version of <code>{"textures":""}</code>
     */
    @Test
    void createOwnerProfileTexturesKeyNotJsonObject() {
        // prepare
        try (
                MockedStatic<Bukkit> bukkitMock = Mockito.mockStatic(Bukkit.class);
        ) {
            final var headName = "testName";
            final var headHeadOwner = UUID.randomUUID();
            final var headValue = "eyJ0ZXh0dXJlcyI6IiJ9";

            final var head = Head.builder()
                    .name(headName)
                    .headOwner(headHeadOwner)
                    .value(headValue)
                    .build();

            final var ownerProfileMock = mock(PlayerProfile.class);

            doReturn(20)
                    .when(mcVersion).minor();
            bukkitMock.when(() -> Bukkit.createPlayerProfile(headHeadOwner, headName))
                    .thenReturn(ownerProfileMock);

            // execute
            final var actual = ownerProfileService.createOwnerProfile(head);

            // verify
            verify(appenderMock).append(captorLoggingEvent.capture());
            final var logEvents = captorLoggingEvent.getAllValues();
            assertThat(logEvents, hasSize(1));
            assertThat(logEvents.get(0).getMessage().getFormattedMessage(), is("Key \"textures\" doesn't contain a JSON object: \"\""));

            verifyNoInteractions(ownerProfileMock);
            assertThat(actual, is(ownerProfileMock));
        }
    }

    /**
     * Test if a warning was logged for using the wrong key,
     * using the base64 encoded version of <code>{"texture":{}}</code>
     */
    @Test
    void createOwnerProfileTexturesKeyTypo() {
        // prepare
        try (
                MockedStatic<Bukkit> bukkitMock = Mockito.mockStatic(Bukkit.class);
        ) {
            final var headName = "testName";
            final var headHeadOwner = UUID.randomUUID();
            final var headValue = "eyJ0ZXh0dXJlIjp7fX0=";

            final var head = Head.builder()
                    .name(headName)
                    .headOwner(headHeadOwner)
                    .value(headValue)
                    .build();

            final var ownerProfileMock = mock(PlayerProfile.class);

            doReturn(20)
                    .when(mcVersion).minor();
            bukkitMock.when(() -> Bukkit.createPlayerProfile(headHeadOwner, headName))
                    .thenReturn(ownerProfileMock);

            // execute
            final var actual = ownerProfileService.createOwnerProfile(head);

            // verify
            verify(appenderMock).append(captorLoggingEvent.capture());
            final var logEvents = captorLoggingEvent.getAllValues();
            assertThat(logEvents, hasSize(1));
            assertThat(logEvents.get(0).getMessage().getFormattedMessage(), is("Key \"textures\" not found"));

            verifyNoInteractions(ownerProfileMock);
            assertThat(actual, is(ownerProfileMock));
        }
    }

    /**
     * Test if a warning was logged for not supplying a json object,
     * using the base64 encoded version of <code>bla</code>.
     */
    @Test
    void createOwnerProfileNotJsonObject() {
        // prepare
        try (
                MockedStatic<Bukkit> bukkitMock = Mockito.mockStatic(Bukkit.class);
        ) {
            final var headName = "testName";
            final var headHeadOwner = UUID.randomUUID();
            final var headValue = "Ymxh";
            final var decodedHeadValue = "bla";

            final var head = Head.builder()
                    .name(headName)
                    .headOwner(headHeadOwner)
                    .value(headValue)
                    .build();

            final var ownerProfileMock = mock(PlayerProfile.class);

            doReturn(20)
                    .when(mcVersion).minor();
            bukkitMock.when(() -> Bukkit.createPlayerProfile(headHeadOwner, headName))
                    .thenReturn(ownerProfileMock);

            // execute
            final var actual = ownerProfileService.createOwnerProfile(head);

            // verify
            verify(appenderMock).append(captorLoggingEvent.capture());
            final var logEvents = captorLoggingEvent.getAllValues();
            assertThat(logEvents, hasSize(1));
            assertThat(logEvents.get(0).getMessage().getFormattedMessage(), is("Root element doesn't contain a JSON object: \"" + decodedHeadValue + "\""));

            verifyNoInteractions(ownerProfileMock);
            assertThat(actual, is(ownerProfileMock));
        }
    }

    /**
     * Test if a warning was logged for providing an invalid json string,
     * using the base64 encoded version of <code>}bla</code>.
     * <p>
     * Normally this would trigger a MalformedJsonException, but this should be catched and a warning should be thrown instead
     */
    @Test
    void createOwnerProfileNotJson() {
        // prepare
        try (
                MockedStatic<Bukkit> bukkitMock = Mockito.mockStatic(Bukkit.class);
        ) {
            final var headName = "testName";
            final var headHeadOwner = UUID.randomUUID();
            final var headValue = "fWJsYQ==";
            final var decodedHeadValue = "}bla";

            final var head = Head.builder()
                    .name(headName)
                    .headOwner(headHeadOwner)
                    .value(headValue)
                    .build();

            final var ownerProfileMock = mock(PlayerProfile.class);

            doReturn(20)
                    .when(mcVersion).minor();
            bukkitMock.when(() -> Bukkit.createPlayerProfile(headHeadOwner, headName))
                    .thenReturn(ownerProfileMock);

            // execute
            final var actual = ownerProfileService.createOwnerProfile(head);

            // verify
            verify(appenderMock).append(captorLoggingEvent.capture());
            final var logEvents = captorLoggingEvent.getAllValues();
            assertThat(logEvents, hasSize(1));
            final var message = logEvents.get(0).getMessage().getFormattedMessage();
            assertThat(message, matchesPattern("Unable to parse \"" + decodedHeadValue + "\" as JSON: com.google.gson.stream.MalformedJsonException: .*"));

            verifyNoInteractions(ownerProfileMock);
            assertThat(actual, is(ownerProfileMock));
        }
    }

    /**
     * Test if a warning was logged for using illegal characters in a base64 string,
     * using <code>;bla</code> as the input
     */
    @Test
    void createOwnerProfileNotBase64() {
        // prepare
        try (
                MockedStatic<Bukkit> bukkitMock = Mockito.mockStatic(Bukkit.class);
        ) {
            final var headName = "testName";
            final var headHeadOwner = UUID.randomUUID();
            final var headValue = ";bla";

            final var head = Head.builder()
                    .name(headName)
                    .headOwner(headHeadOwner)
                    .value(headValue)
                    .build();

            final var ownerProfileMock = mock(PlayerProfile.class);

            doReturn(20)
                    .when(mcVersion).minor();
            bukkitMock.when(() -> Bukkit.createPlayerProfile(headHeadOwner, headName))
                    .thenReturn(ownerProfileMock);

            // execute
            final var actual = ownerProfileService.createOwnerProfile(head);

            // verify
            verify(appenderMock).append(captorLoggingEvent.capture());
            final var logEvents = captorLoggingEvent.getAllValues();
            assertThat(logEvents, hasSize(1));
            assertThat(logEvents.get(0).getMessage().getFormattedMessage(), is("Unable to base64 decode \"" + headValue + "\": Illegal base64 character 3b"));

            verifyNoInteractions(ownerProfileMock);
            assertThat(actual, is(ownerProfileMock));
        }
    }
}