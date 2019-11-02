package com.github.cc007.headsplugin.presentation.commands.headsplugin;

import com.github.cc007.headsplugin.HeadsPlugin;
import com.github.cc007.headsplugin.config.PluginVersionProvider;
import com.github.cc007.headsplugin.presentation.commands.AbstractCommand;
import dev.alangomes.springspigot.command.Subcommand;
import org.bukkit.command.ConsoleCommandSender;
import org.springframework.beans.factory.annotation.Autowired;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Subcommand
@Command(
        name = "update",
        versionProvider = PluginVersionProvider.class,
        mixinStandardHelpOptions = true,
        description = "Update all or a specific category",
        aliases = {"up", "updateheads", "uh", "uphds"}

)
public class UpdateCategoryCommand extends AbstractCommand {
    @Autowired
    private HeadsPlugin headsPlugin;

    @Parameters(
            index = "0",
            defaultValue = "all",
            //completionCandidates = ..., TODO complete from category names
            description = "The category to update. If this parameter is not specified, all categories will be updated",
            paramLabel = "categoryName"
    )
    private String categoryName;

    @Override
    public void run() {
        if (context.getPlayer() != null) {
            context.getPlayer().sendMessage(chatManager.getChatPrefix() + "This might actually do stuff to " + categoryName + " in the future");
        } else if (context.getSender() instanceof ConsoleCommandSender) {
            context.getSender().sendMessage(chatManager.getConsolePrefix() + "This might actually do stuff to " + categoryName + " in the future");
        }
    }
}