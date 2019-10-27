package com.github.cc007.headsplugin.presentation.commands.headsplugin;

import com.github.cc007.headsplugin.HeadsPlugin;
import com.github.cc007.headsplugin.business.domain.Head;
import com.github.cc007.headsplugin.config.PluginVersionProvider;
import com.github.cc007.headsplugin.integration.daos.heads.MinecraftHeadsDao;
import com.github.cc007.headsplugin.presentation.commands.AbstractCommand;
import dev.alangomes.springspigot.command.Subcommand;
import lombok.val;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.nio.charset.StandardCharsets;

@Subcommand
@Command(
        name = "list",
        versionProvider = PluginVersionProvider.class,
        mixinStandardHelpOptions = true,
        description = "Command to list the available heads from mineskin in chat (for test purposes",
        aliases = {"lst"}

)
public class MineSkinApiTestCommand extends AbstractCommand {
    @Autowired
    private HeadsPlugin headsPlugin;

    @Autowired
    private MinecraftHeadsDao minecraftHeadsDao;

    @Parameters(
            index = "0",
            description = "The search term to find the heads",
            paramLabel = "searchTerm"
    )
    private String searchTerm;

    @Override
    public void run() {
        if (context.getPlayer() == null) {
            context.getSender().sendMessage(chatManager.getConsolePrefix() + "This command is only available for players.");
        }

        val heads = minecraftHeadsDao.getCategoryHeads(minecraftHeadsDao.getCategoryNames().get(0));
        context.getPlayer().sendMessage(chatManager.getChatPrefix() + "The following heads are available:");
        for (Head head : heads) {
            context.getPlayer().sendMessage("   - " + head.getName());
            context.getPlayer().sendMessage("      db: " + head.getHeadDatabase());
            context.getPlayer().sendMessage("      owner: " + head.getHeadOwner());
            context.getPlayer().sendMessage("      value: " + new String(Base64.decodeBase64(head.getValue()), StandardCharsets.UTF_8));
        }
    }
}