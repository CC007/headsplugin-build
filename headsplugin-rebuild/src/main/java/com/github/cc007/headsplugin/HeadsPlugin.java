package com.github.cc007.headsplugin;

import com.github.cc007.headsplugin.api.HeadsPluginApi;
import com.github.cc007.headsplugin.dagger.DaggerHeadsPluginComponent;
import com.github.cc007.headsplugin.dagger.HeadsPluginComponent;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import lombok.var;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
import java.util.Optional;

@Log4j2
public class HeadsPlugin extends JavaPlugin implements HeadsPluginApi {

    private HeadsPluginComponent headsPluginComponent;
    private ClassLoader defaultClassLoader;

    /**
     * Gets the instance of the HeadsPlugin
     *
     * @return Optional of the HeadsPlugin plugin
     */
    public static Optional<HeadsPlugin> getPlugin() {
        return HeadsPluginApi.getPlugin("HeadsPluginAPI")
                .filter(plugin -> plugin instanceof HeadsPlugin)
                .map(plugin -> (HeadsPlugin) plugin);
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();

        defaultClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(getClassLoader());
        headsPluginComponent = DaggerHeadsPluginComponent.create();

        //StartupCategoryUpdater startupCategoryUpdater = headsPluginComponent.startupCategoryUpdater();
        //startupCategoryUpdater.update();
    }

    @Override
    public void onDisable() {
        shutdownDatabase();
        Thread.currentThread().setContextClassLoader(defaultClassLoader);
    }

    @Override
    public HeadsPluginComponent getHeadsPluginServices() {
        return headsPluginComponent;
    }

    private void shutdownDatabase() {
        val entityManager = getHeadsPluginServices().entityManager();
        entityManager.getTransaction().begin();
        entityManager.createNativeQuery("CHECKPOINT;").executeUpdate();
        entityManager.createNativeQuery("SHUTDOWN;").executeUpdate();
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    private String prettyPrint(String string, boolean newLineBeforeClosingBracket) {
        val sb = new StringBuilder().append('\n');
        val sr = new StringReader(string);
        prettyPrint(sr, sb, 0, newLineBeforeClosingBracket);
        return sb.toString();
    }

    @SneakyThrows(IOException.class)
    private int prettyPrint(StringReader sr, StringBuilder sb, int indent, boolean newLineBeforeClosingBracket) {
        var character = sr.read();
        while (character != -1) {
            if (character == ']' || character == ')') {
                return character;
            }
            if (character == '[' || character == '(') {
                sb.append((char) character);
                println(sb, indent + 2);

                character = prettyPrint(sr, sb, indent + 2, newLineBeforeClosingBracket);
                if (newLineBeforeClosingBracket) {
                    println(sb, indent);
                }
            }
            sb.append((char) character);

            if (character == ',') {
                println(sb, indent);
                // skip space
                sr.read();
            }
            character = sr.read();
        }
        return character;
    }

    private void println(StringBuilder sb, int indent) {
        sb.append('\n')
                .append(String.join("", Collections.nCopies(indent, " ")));
    }
}
