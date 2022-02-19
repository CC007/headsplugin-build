package com.github.cc007.headsplugin.api;

import com.github.npathai.hamcrestopt.OptionalMatchers;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.Optional;

import static com.github.npathai.hamcrestopt.OptionalMatchers.isEmpty;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresentAndIs;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

class HeadsPluginApiTest {

    @Test
    void getPlugin() {
        try (MockedStatic<HeadsPluginApi> headsPluginApi = mockStatic(HeadsPluginApi.class)) {
            // prepare
            final var plugin = mock(Plugin.class);

            headsPluginApi.when(() -> HeadsPluginApi.getPlugin("HeadsPluginAPI"))
                    .thenReturn(Optional.of(plugin));
            headsPluginApi.when(HeadsPluginApi::getPlugin)
                    .thenCallRealMethod();

            // execute
            final var actual = HeadsPluginApi.getPlugin();

            // verify
            assertThat(actual, OptionalMatchers.isPresentAndIs(plugin));
        }
    }

    @Test
    void getPluginNotFound() {
        try (MockedStatic<HeadsPluginApi> headsPluginApi = mockStatic(HeadsPluginApi.class)) {
            // prepare
            headsPluginApi.when(() -> HeadsPluginApi.getPlugin("HeadsPluginAPI"))
                    .thenReturn(Optional.empty());
            headsPluginApi.when(HeadsPluginApi::getPlugin)
                    .thenCallRealMethod();

            // execute
            final var actual = HeadsPluginApi.getPlugin();

            // verify
            assertThat(actual, isEmpty());
        }
    }

    @Test
    void getPluginWithName() {
        try (MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class)) {
            // prepare
            final var pluginName = "TestPluginName";

            final var server = mock(Server.class);
            final var pluginManager = mock(PluginManager.class);
            final var plugin = mock(Plugin.class);

            bukkit.when(Bukkit::getServer)
                    .thenReturn(server);
            when(server.getPluginManager())
                    .thenReturn(pluginManager);
            when(pluginManager.getPlugin(pluginName))
                    .thenReturn(plugin);
            when(plugin.isEnabled())
                    .thenReturn(true);


            // execute
            final var actual = HeadsPluginApi.getPlugin(pluginName);

            // verify
            assertThat(actual, OptionalMatchers.isPresentAndIs(plugin));
        }
    }

    @Test
    void getPluginWithNameNotEnabled() {
        try (MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class)) {
            // prepare
            final var pluginName = "TestPluginName";

            final var server = mock(Server.class);
            final var pluginManager = mock(PluginManager.class);
            final var plugin = mock(Plugin.class);

            bukkit.when(Bukkit::getServer)
                    .thenReturn(server);
            when(server.getPluginManager())
                    .thenReturn(pluginManager);
            when(pluginManager.getPlugin(pluginName))
                    .thenReturn(plugin);
            when(plugin.isEnabled())
                    .thenReturn(false);


            // execute
            final var actual = HeadsPluginApi.getPlugin(pluginName);

            // verify
            assertThat(actual, isEmpty());
        }
    }

    @Test
    void getPluginWithNameNotFound() {
        try (MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class)) {
            // prepare
            final var pluginName = "TestPluginName";

            final var server = mock(Server.class);
            final var pluginManager = mock(PluginManager.class);

            bukkit.when(Bukkit::getServer)
                    .thenReturn(server);
            when(server.getPluginManager())
                    .thenReturn(pluginManager);
            when(pluginManager.getPlugin(pluginName))
                    .thenReturn(null);


            // execute
            final var actual = HeadsPluginApi.getPlugin(pluginName);

            // verify
            assertThat(actual, isEmpty());
        }
    }

    @Test
    void getAndSetHeadsPluginServices() {
        // prepare
        final var headsPluginServices = mock(HeadsPluginServices.class);

        // execute
        final var actual1 = HeadsPluginApi.getHeadsPluginServices();
        HeadsPluginApi.setHeadsPluginServices(headsPluginServices);
        final var actual2 = HeadsPluginApi.getHeadsPluginServices();

        // verify
        assertThat(actual1, isEmpty());
        assertThat(actual2, isPresentAndIs(headsPluginServices));
    }

    @Test
    void setHeadsPluginServicesNull() {
        // prepare

        // execute
        final var actualException = Assertions.assertThrows(NullPointerException.class,
                () -> HeadsPluginApi.setHeadsPluginServices(null)
        );

        // verify
        assertThat(actualException.getMessage(), containsString("is marked non-null but is null"));
    }
}