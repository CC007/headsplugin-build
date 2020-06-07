package com.github.cc007.headsplugin.presentation.commands;

import com.github.cc007.headsplugin.config.PluginVersionProvider;
import com.github.cc007.headsplugin.presentation.commands.headsplugin.ListCategoriesCommand;
import com.github.cc007.headsplugin.presentation.commands.headsplugin.ShowCategoriesCommand;
import com.github.cc007.headsplugin.presentation.commands.headsplugin.UpdateCategoryCommand;

import dev.alangomes.springspigot.command.CommandExecutor;
import org.bukkit.ChatColor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;

@Component
@Command(
        name = "headspluginapi",
        versionProvider = PluginVersionProvider.class,
        description = "Provides the commands for HeadsPlugin",
        aliases = {"headsplugin", "hdspluginapi", "hdsplugin", "hpa", "hp"},
        mixinStandardHelpOptions = true,
        subcommands = {
                ListCategoriesCommand.class,
                ShowCategoriesCommand.class,
                UpdateCategoryCommand.class
        }
)
public class HeadsPluginCommand extends AbstractCommand {

    @Autowired
    private CommandExecutor commandExecutor;

    @Override
    protected final void handleCommand() {
        context.getSender().sendMessage(chatManager.getPrefix() + ChatColor.RED + "Missing parameter!");
        commandExecutor.execute("hpa", "-h");
    }
}
