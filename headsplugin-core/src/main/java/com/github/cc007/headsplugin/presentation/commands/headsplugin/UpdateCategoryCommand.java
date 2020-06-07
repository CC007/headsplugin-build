package com.github.cc007.headsplugin.presentation.commands.headsplugin;

import com.github.cc007.headsplugin.api.business.services.heads.CategoryUpdater;
import com.github.cc007.headsplugin.config.PluginVersionProvider;
import com.github.cc007.headsplugin.presentation.commands.AbstractCommand;

import dev.alangomes.springspigot.command.CommandExecutor;
import dev.alangomes.springspigot.command.Subcommand;
import dev.alangomes.springspigot.security.Authorize;
import dev.alangomes.springspigot.security.HasPermission;
import org.bukkit.ChatColor;
import org.springframework.beans.factory.annotation.Autowired;
import picocli.CommandLine;
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
    private CategoryUpdater categoryUpdater;

    @Autowired
    private CommandExecutor commandExecutor;

    @Parameters(
            index = "0",
            defaultValue = "all",
            //completionCandidates = ..., TODO complete from category names
            description = "The category to update. If this parameter is not specified or if the parameter specifies 'all', all categories will be updated",
            paramLabel = "categoryName"
    )
    private String categoryName;

    @Override
    @HasPermission(value = "headspluginapi.update", message = "You don't have the permission to update categories!")
    public void run() {
        if (handleHelpAndVersion()) {
            return;
        }
        handleCommand();
    }

    @Override
    protected final void handleCommand() {
        //TODO perms for updating categories
        if (categoryName == null || categoryName.equals("all") || categoryName.equals("*")) {
            updateAllCategories();
        } else {
            updateCategory(categoryName);
        }
    }

    private void updateCategory(String categoryName) {
        context.getSender().sendMessage(chatManager.getPrefix() + "Updating " + categoryName + " category...");
        try {
            categoryUpdater.updateCategory(categoryName);
            context.getSender().sendMessage(chatManager.getPrefix() + categoryName + " category is now updated.");
        } catch (IllegalArgumentException ex) {
            context.getSender().sendMessage(chatManager.getPrefix() + ChatColor.RED + categoryName + " category doesn't exist.");
            commandExecutor.execute("hpa", "show");
        }
    }

    private void updateAllCategories() {
        context.getSender().sendMessage(chatManager.getPrefix() + "Updating all categories...");
        categoryUpdater.updateCategories();
        context.getSender().sendMessage(chatManager.getPrefix() + "All categories are now updated.");
    }
}
