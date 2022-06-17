package com.github.cc007.headsplugin.presentation.commands.headsplugin;

import com.github.cc007.headsplugin.api.business.services.heads.CategoryUpdater;
import com.github.cc007.headsplugin.config.PluginVersionProvider;
import com.github.cc007.headsplugin.presentation.commands.AbstractCommand;

import dev.alangomes.springspigot.command.CommandExecutor;
import dev.alangomes.springspigot.command.Subcommand;
import dev.alangomes.springspigot.security.HasPermission;
import dev.alangomes.springspigot.util.scheduler.SchedulerService;
import org.bukkit.ChatColor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.util.Collection;

@Subcommand
@Command(
        name = "update",
        versionProvider = PluginVersionProvider.class,
        mixinStandardHelpOptions = true,
        usageHelpWidth = 60,
        description = "Update all or a specific category",
        aliases = {"up", "updateheads", "uh", "uphds"}

)
public class UpdateCategoryCommand extends AbstractCommand {

    /**
     * The delay in seconds between category updates
     */
    @Value("${headsplugin.categories.update.delay:10}")
    private int delayBetweenCategoryUpdates = 10;

    @Autowired
    private CategoryUpdater categoryUpdater;

    @Autowired
    private CommandExecutor commandExecutor;

    @Autowired
    private SchedulerService schedulerService;

    @Parameters(
            index = "0",
            defaultValue = "all",
            completionCandidates = UpdateCategoryCompletionCandidates.class,
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
        if (categoryName == null || categoryName.equals("all") || categoryName.equals("*")) {
            updateAllCategories();
        } else {
            updateCategory(categoryName);
        }
    }

    private void updateCategory(String categoryName) {
        try {
            context.getSender().sendMessage(chatManager.getPrefix() + "Updating " + categoryName + " category...");
            categoryUpdater.updateCategory(categoryName);
            context.getSender().sendMessage(chatManager.getPrefix() + categoryName + " category is now updated.");
        } catch (IllegalArgumentException ex) {
            context.getSender().sendMessage(chatManager.getPrefix() + ChatColor.RED + categoryName + " category doesn't exist.");
            commandExecutor.execute("hpa", "show");
        }
    }

    private void updateAllCategories() {
        Collection<String> updatableCategoryNames = categoryUpdater.getUpdatableCategoryNames(false);
        context.getSender().sendMessage(
                chatManager.getPrefix() + "Updating all categories (" + String.join(", ", updatableCategoryNames) + ")..."
        );
        int delay = 0;
        for (String updatableCategoryName : updatableCategoryNames) {
            schedulerService.scheduleSyncDelayedTask(() -> {
                categoryUpdater.updateCategory(updatableCategoryName);
                context.getSender().sendMessage(chatManager.getPrefix() + updatableCategoryName + " category is now updated.");
            }, delay);
            delay += 20 * delayBetweenCategoryUpdates;
        }
        context.getSender().sendMessage(
                chatManager.getPrefix() + "All categories updates are now scheduled (with " + delayBetweenCategoryUpdates + " seconds between each category)."
        );
    }
}
