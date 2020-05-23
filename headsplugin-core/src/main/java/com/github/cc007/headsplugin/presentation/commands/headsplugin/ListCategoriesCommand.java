package com.github.cc007.headsplugin.presentation.commands.headsplugin;

import com.github.cc007.headsplugin.api.business.domain.Category;
import com.github.cc007.headsplugin.api.business.services.heads.CategorySearcher;
import com.github.cc007.headsplugin.business.services.chat.ChatManager;
import com.github.cc007.headsplugin.config.PluginVersionProvider;
import com.github.cc007.headsplugin.presentation.commands.AbstractCommand;

import dev.alangomes.springspigot.command.Subcommand;
import dev.alangomes.springspigot.context.Context;
import picocli.CommandLine.Command;

@Subcommand
@Command(
        name = "listcategories",
        versionProvider = PluginVersionProvider.class,
        mixinStandardHelpOptions = true,
        description = "Show a list of all categories",
        aliases = {"listcat", "lstcat", "list", "lst"}

)
public class ListCategoriesCommand extends AbstractCommand {

    private final CategorySearcher categorySearcher;

    public ListCategoriesCommand(
            CategorySearcher categorySearcher,
            Context context,
            ChatManager chatManager) {
        super(context, chatManager);
        this.categorySearcher = categorySearcher;
    }

    @Override
    public void run() {
        StringBuilder sb = new StringBuilder("List of all categories:\n");
        for (Category category : categorySearcher.getCategories()) {
            String categoryString = " - " + category.getName() + " (" + String.join(", ", category.getDatabaseNames()) + ")\n";
            sb.append(categoryString);
        }
        context.getSender().sendMessage(chatManager.getPrefix() + sb.toString());
    }
}
