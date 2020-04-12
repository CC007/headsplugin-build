package com.github.cc007.headsplugin.presentation.commands.headsplugin;

import com.github.cc007.headsplugin.api.business.services.heads.CategoryUpdater;
import com.github.cc007.headsplugin.business.services.chat.ChatManager;
import com.github.cc007.headsplugin.config.PluginVersionProvider;
import com.github.cc007.headsplugin.presentation.commands.AbstractCommand;

import dev.alangomes.springspigot.command.Subcommand;
import dev.alangomes.springspigot.context.Context;
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

    private final CategoryUpdater categoryUpdater;

    @Parameters(
            index = "0",
            defaultValue = "all",
            //completionCandidates = ..., TODO complete from category names
            description = "The category to update. If this parameter is not specified or if the parameter specifies 'all', all categories will be updated",
            paramLabel = "categoryName"
    )
    private String categoryName;

    public UpdateCategoryCommand(
            CategoryUpdater categoryUpdater,
            Context context,
            ChatManager chatManager) {
        super(context, chatManager);
        this.categoryUpdater = categoryUpdater;
    }

    @Override
    public void run() {
        if(categoryName == null || categoryName.equals("all")) {
            context.getSender().sendMessage(chatManager.getPrefix() + "Updating all categories...");
            categoryUpdater.updateCategories();
            context.getSender().sendMessage(chatManager.getPrefix() + "All categories are now updated.");
        } else {
            context.getSender().sendMessage(chatManager.getPrefix() + "Updating " + categoryName + " category...");
            categoryUpdater.updateCategory(categoryName);
            context.getSender().sendMessage(chatManager.getPrefix() + categoryName + " category is now updated.");
        }
    }
}
