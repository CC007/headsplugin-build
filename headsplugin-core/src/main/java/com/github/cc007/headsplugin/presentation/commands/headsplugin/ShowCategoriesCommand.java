package com.github.cc007.headsplugin.presentation.commands.headsplugin;

import com.github.cc007.headsplugin.api.business.domain.Category;
import com.github.cc007.headsplugin.api.business.services.heads.CategorySearcher;
import com.github.cc007.headsplugin.config.PluginVersionProvider;
import com.github.cc007.headsplugin.presentation.commands.AbstractCommand;

import dev.alangomes.springspigot.command.Subcommand;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.core.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import picocli.CommandLine.Command;

@Subcommand
@Command(
        name = "showcategories",
        versionProvider = PluginVersionProvider.class,
        mixinStandardHelpOptions = true,
        usageHelpWidth = 60,
        description = "Show a list of all categories",
        aliases = {"showcat", "shwcat", "show", "shw"}

)
@Log4j2
public class ShowCategoriesCommand extends AbstractCommand {

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
