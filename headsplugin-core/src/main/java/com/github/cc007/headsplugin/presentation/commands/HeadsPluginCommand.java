package com.github.cc007.headsplugin.presentation.commands;

import com.github.cc007.headsplugin.business.services.chat.ChatManager;
import com.github.cc007.headsplugin.config.PluginVersionProvider;
import com.github.cc007.headsplugin.presentation.commands.headsplugin.DbTestCommand;
import com.github.cc007.headsplugin.presentation.commands.headsplugin.UpdateCategoryCommand;

import dev.alangomes.springspigot.context.Context;
import org.springframework.stereotype.Component;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Help.Ansi;

@Component
@Command(
        name = "headspluginapi",
        versionProvider = PluginVersionProvider.class,
        description = "Provides the commands for HeadsPlugin",
        aliases = {"headsplugin", "hdspluginapi", "hdsplugin", "hpa", "hp"},
        mixinStandardHelpOptions = true,
        subcommands = {
                UpdateCategoryCommand.class,
                DbTestCommand.class
        }
)
public class HeadsPluginCommand extends AbstractCommand {

    public HeadsPluginCommand(Context context, ChatManager chatManager) {
        super(context, chatManager);
    }

    @Override
    public void run() {
        CommandLine cmd = commandSpec.commandLine();
        if (cmd.isUsageHelpRequested() || cmd.isVersionHelpRequested()) {
            context.getPlayer().sendMessage(chatManager.getChatBanner());
            if (cmd.isVersionHelpRequested()) {
                context.getPlayer().sendMessage(commandSpec.version());
            }
            if (cmd.isUsageHelpRequested()) {
                context.getPlayer().sendMessage(cmd.getUsageMessage(Ansi.OFF) + "\n ");
            }
        }
    }
}
