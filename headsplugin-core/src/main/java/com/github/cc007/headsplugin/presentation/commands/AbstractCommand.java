package com.github.cc007.headsplugin.presentation.commands;

import com.github.cc007.headsplugin.business.services.chat.ChatManager;

import dev.alangomes.springspigot.context.Context;
import lombok.RequiredArgsConstructor;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

@RequiredArgsConstructor
public abstract class AbstractCommand implements Runnable {
    @Spec
    protected CommandSpec commandSpec;

    protected final Context context;
    protected final ChatManager chatManager;
}
