package com.github.cc007.headsplugin.presentation.commands;

import com.github.cc007.headsplugin.config.PluginVersionProvider;
import com.github.cc007.headsplugin.presentation.commands.headsplugin.CategoriesPropertiesTestCommand;
import com.github.cc007.headsplugin.presentation.commands.headsplugin.ListCategoriesCommand;
import com.github.cc007.headsplugin.presentation.commands.headsplugin.ShowCategoriesCommand;
import com.github.cc007.headsplugin.presentation.commands.headsplugin.UpdateCategoryCommand;

import dev.alangomes.springspigot.command.CommandExecutor;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import javax.annotation.PostConstruct;

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
                UpdateCategoryCommand.class,
                CategoriesPropertiesTestCommand.class
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
