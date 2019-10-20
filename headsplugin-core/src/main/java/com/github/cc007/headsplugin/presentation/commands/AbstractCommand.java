package com.github.cc007.headsplugin.presentation.commands;

import com.github.cc007.headsplugin.business.services.chat.ChatManager;
import dev.alangomes.springspigot.context.Context;
import org.springframework.beans.factory.annotation.Autowired;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

public abstract class AbstractCommand implements Runnable {
    @Spec
    protected CommandSpec commandSpec;

    @Autowired
    protected Context context;

    @Autowired
    protected ChatManager chatManager;
}
