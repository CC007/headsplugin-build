package com.github.cc007.headsplugin.presentation.commands.headsplugin;

import com.github.cc007.headsplugin.api.business.domain.Category;
import com.github.cc007.headsplugin.api.business.services.heads.CategorySearcher;
import com.github.cc007.headsplugin.config.PluginVersionProvider;
import com.github.cc007.headsplugin.presentation.commands.AbstractCommand;

import dev.alangomes.springspigot.command.Subcommand;
import org.springframework.beans.factory.annotation.Autowired;
import picocli.CommandLine.Command;

@Subcommand
@Command(
        name = "listcategories",
        versionProvider = PluginVersionProvider.class,
        mixinStandardHelpOptions = true,
        usageHelpWidth = 60,
        description = "Show a list of all categories",
        aliases = {"listcat", "lstcat", "list", "lst"}

)
public class ListCategoriesCommand extends AbstractCommand {

    @Autowired
    private CategorySearcher categorySearcher;

    @Override
    protected final void handleCommand() {
        StringBuilder sb = new StringBuilder("List of all categories:\n");
        for (Category category : categorySearcher.getCategories()) {
            String categoryString = " - " + category.getName() + " (" + String.join(", ", category.getSources()) + ")\n";
            sb.append(categoryString);
        }
        context.getSender().sendMessage(chatManager.getPrefix() + sb.toString());
    }
}
