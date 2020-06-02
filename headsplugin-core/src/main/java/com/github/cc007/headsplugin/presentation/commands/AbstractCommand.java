package com.github.cc007.headsplugin.presentation.commands;

import com.github.cc007.headsplugin.business.services.chat.ChatManager;

import dev.alangomes.springspigot.context.Context;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import picocli.CommandLine;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

import javax.annotation.PostConstruct;

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
                context.getSender().sendMessage(cmd.getUsageMessage(CommandLine.Help.Ansi.OFF) + "\n ");
            }
            return true;
        }
        return false;
    }

    @Override
    public final void run() {
        if (handleHelpAndVersion()) {
            return;
        }
        handleCommand();
    }
}
