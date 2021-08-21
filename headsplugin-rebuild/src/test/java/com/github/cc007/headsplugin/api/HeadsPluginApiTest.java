package com.github.cc007.headsplugin.api;

import com.github.cc007.headsplugin.HeadsPlugin;

import com.github.npathai.hamcrestopt.OptionalMatchers;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

class HeadsPluginApiTest {

    @Test
    void getPlugin() {
        try (MockedStatic<HeadsPluginApi> headsPluginApi = mockStatic(HeadsPluginApi.class)) {
            // prepare
            HeadsPlugin plugin = mock(HeadsPlugin.class);

            headsPluginApi.when(() -> HeadsPluginApi.getPlugin("HeadsPluginAPI"))
                    .thenReturn(Optional.of(plugin));
            headsPluginApi.when(HeadsPluginApi::getPlugin)
                    .thenCallRealMethod();

            // execute
            Optional<HeadsPluginApi> actual = HeadsPluginApi.getPlugin();

            // verify
            assertThat(actual, OptionalMatchers.isPresentAndIs(plugin));
        }
    }

    @Test
    void getPluginOtherPlugin() {
        try (MockedStatic<HeadsPluginApi> headsPluginApi = mockStatic(HeadsPluginApi.class)) {
            // prepare
            Plugin plugin = mock(Plugin.class);

            headsPluginApi.when(() -> HeadsPluginApi.getPlugin("HeadsPluginAPI"))
                    .thenReturn(Optional.of(plugin));
            headsPluginApi.when(HeadsPluginApi::getPlugin)
                    .thenCallRealMethod();

            // execute
            Optional<HeadsPluginApi> actual = HeadsPluginApi.getPlugin();

            // verify
            assertThat(actual, OptionalMatchers.isEmpty());
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
            Optional<HeadsPluginApi> actual = HeadsPluginApi.getPlugin();

            // verify
            assertThat(actual, OptionalMatchers.isEmpty());
        }
    }

    @Test
    void getPluginWithName() {
        try (MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class)) {
            // prepare
            String pluginName = "TestPluginName";

            Server server = mock(Server.class);
            PluginManager pluginManager = mock(PluginManager.class);
            Plugin plugin = mock(Plugin.class);

            bukkit.when(Bukkit::getServer)
                    .thenReturn(server);
            when(server.getPluginManager())
                    .thenReturn(pluginManager);
            when(pluginManager.getPlugin(pluginName))
                    .thenReturn(plugin);
            when(plugin.isEnabled())
                    .thenReturn(true);


            // execute
            Optional<Plugin> actual = HeadsPluginApi.getPlugin(pluginName);

            // verify
            assertThat(actual, OptionalMatchers.isPresentAndIs(plugin));
        }
    }

    @Test
    void getPluginWithNameNotEnabled() {
        try (MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class)) {
            // prepare
            String pluginName = "TestPluginName";

            Server server = mock(Server.class);
            PluginManager pluginManager = mock(PluginManager.class);
            Plugin plugin = mock(Plugin.class);

            bukkit.when(Bukkit::getServer)
                    .thenReturn(server);
            when(server.getPluginManager())
                    .thenReturn(pluginManager);
            when(pluginManager.getPlugin(pluginName))
                    .thenReturn(plugin);
            when(plugin.isEnabled())
                    .thenReturn(false);


            // execute
            Optional<Plugin> actual = HeadsPluginApi.getPlugin(pluginName);

            // verify
            assertThat(actual, OptionalMatchers.isEmpty());
        }
    }

    @Test
    void getPluginWithNameNotFound() {
        try (MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class)) {
            // prepare
            String pluginName = "TestPluginName";

            Server server = mock(Server.class);
            PluginManager pluginManager = mock(PluginManager.class);

            bukkit.when(Bukkit::getServer)
                    .thenReturn(server);
            when(server.getPluginManager())
                    .thenReturn(pluginManager);
            when(pluginManager.getPlugin(pluginName))
                    .thenReturn(null);


            // execute
            Optional<Plugin> actual = HeadsPluginApi.getPlugin(pluginName);

            // verify
            assertThat(actual, OptionalMatchers.isEmpty());
        }
    }
}