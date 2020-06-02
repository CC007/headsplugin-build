package com.github.cc007.headsplugin.presentation.commands.headsplugin;

import com.github.cc007.headsplugin.config.PluginVersionProvider;
import com.github.cc007.headsplugin.config.properties.CategoriesProperties;
import com.github.cc007.headsplugin.presentation.commands.AbstractCommand;

import dev.alangomes.springspigot.command.Subcommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Subcommand
@Command(
        name = "categoriesconfig",
        aliases = {"cc"},
        versionProvider = PluginVersionProvider.class,
        mixinStandardHelpOptions = true,
        description = "Command to list the available heads from mineskin in chat (for test purposes)"
)
@Slf4j
public class CategoriesPropertiesTestCommand extends AbstractCommand {

    @Autowired
    private CategoriesProperties categoriesProperties;

    @Parameters(
            index = "0",
            description = "The config action that needs to be done",
            paramLabel = "action"
    )
    private String action;

    @Override
    protected final void handleCommand() {
        if (action != null) {
            switch (action) {
                case "updateinterval":
                    context.getSender().sendMessage(
                            chatManager.getPrefix() + "Update interval: " + categoriesProperties.getUpdate().getInterval()
                    );
                    return;
                case "customlist":
                    context.getSender().sendMessage(chatManager.getChatBanner());
                    context.getSender().sendMessage("Custom category names:");
                    for (CategoriesProperties.CustomCategory customCategory : categoriesProperties.getCustom()) {
                        context.getSender().sendMessage(" - " + customCategory.getName()
                                + " (" + String.join(", ", customCategory.getSearchTerms()) + ")");
                    }
                    return;
            }
        }
        context.getSender().sendMessage(
                chatManager.getPrefix() + "Unknown subcommand! " +
                        "Use '/hpa categoriesconfig updateinterval' or '/hpa categoriesconfig customlist'"
        );
    }
}