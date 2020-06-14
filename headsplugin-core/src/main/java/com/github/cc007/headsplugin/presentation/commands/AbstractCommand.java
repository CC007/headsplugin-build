package com.github.cc007.headsplugin.presentation.commands;

import com.github.cc007.headsplugin.business.services.chat.ChatManager;

import dev.alangomes.springspigot.context.Context;
import lombok.extern.log4j.Log4j2;
import org.bukkit.ChatColor;
import org.springframework.beans.factory.annotation.Autowired;
import picocli.CommandLine;
import picocli.CommandLine.Help;
import picocli.CommandLine.IHelpSectionRenderer;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Model.UsageMessageSpec;
import picocli.CommandLine.Spec;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Log4j2
public abstract class AbstractCommand implements Runnable {
    @Spec
    protected CommandSpec commandSpec;

    @Autowired
    protected Context context;

    @Autowired
    protected ChatManager chatManager;

    protected abstract void handleCommand();

    protected final boolean handleHelpAndVersion() {
        CommandLine cmd = commandSpec.commandLine();
        if (cmd.isUsageHelpRequested() || cmd.isVersionHelpRequested()) {
            context.getSender().sendMessage(chatManager.getChatBanner());
            if (cmd.isVersionHelpRequested()) {
                context.getSender().sendMessage(commandSpec.version());
            }
            if (cmd.isUsageHelpRequested()) {
                Map<String, IHelpSectionRenderer> helpSectionMap = cmd.getHelpSectionMap();
                helpSectionMap.put(UsageMessageSpec.SECTION_KEY_SYNOPSIS,help -> {
                    String usage = ChatColor.GOLD + "/" + help.synopsis(help.synopsisHeadingLength()).substring(13);
                    usage = usage.substring(0, usage.length()-2) + ChatColor.RESET + "\n";
                    return usage;
                });
                helpSectionMap.put(UsageMessageSpec.SECTION_KEY_COMMAND_LIST, new HpaCommandListRenderer());
                context.getSender().sendMessage(cmd.getUsageMessage(Help.Ansi.AUTO) + "\n ");
            }
            return true;
        }
        return false;
    }

    @Override
    public void run() {
        if (handleHelpAndVersion()) {
            return;
        }
        handleCommand();
    }

    protected static class HpaCommandListRenderer implements IHelpSectionRenderer {

        @Override
        public String render(Help help) {
            CommandSpec spec = help.commandSpec();
            if (spec.subcommands().isEmpty()) { return ""; }

            StringBuilder commandsList = new StringBuilder();
            Map<String, String> subcommandMap = new HashMap<>();
            for (CommandLine subcommand : spec.subcommands().values()) {
                addSubcommands(subcommand, subcommandMap);
            }
            for (Map.Entry<String, String> subcommand : subcommandMap.entrySet()) {
                commandsList.append(ChatColor.GOLD + "  /" + getQualifiedName(spec) + " " + subcommand.getKey() + "\n" + ChatColor.RESET);
                commandsList.append("  " + subcommand.getValue() + "\n");
                commandsList.append(" \n");
            }
            return commandsList.toString();
        }

        private void addSubcommands(CommandLine subcommand, Map<String, String> subcommandMap) {
            // create comma-separated list of command name and aliases
            Set<String> names = subcommand.getCommandSpec().names();
            String namesString = "(" + String.join(" | ", names) + ")";

            // command description is taken from header or description
            String description = description(subcommand.getCommandSpec().usageMessage());

            // add a line for this command to the layout
            subcommandMap.put(namesString, description);
        }

        private String description(UsageMessageSpec usageMessage) {
            if (usageMessage.header().length > 0) {
                return usageMessage.header()[0];
            }
            if (usageMessage.description().length > 0) {
                return usageMessage.description()[0];
            }
            return "";
        }
    }

    private static String getQualifiedName(CommandSpec spec) {
        return spec.qualifiedName().substring(13);
    }
}
