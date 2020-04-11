package com.github.cc007.headsplugin.presentation.commands.headsplugin;

import com.github.cc007.headsplugin.api.business.domain.Head;
import com.github.cc007.headsplugin.api.business.services.heads.HeadCreator;
import com.github.cc007.headsplugin.api.business.services.heads.HeadPlacer;
import com.github.cc007.headsplugin.api.business.services.heads.HeadSearcher;
import com.github.cc007.headsplugin.business.services.NBTPrinter;
import com.github.cc007.headsplugin.business.services.chat.ChatManager;
import com.github.cc007.headsplugin.config.PluginVersionProvider;
import com.github.cc007.headsplugin.integration.database.mappers.from_entity.HeadEntityToHeadMapper;
import com.github.cc007.headsplugin.integration.database.repositories.HeadRepository;
import com.github.cc007.headsplugin.presentation.commands.AbstractCommand;

import dev.alangomes.springspigot.command.Subcommand;
import dev.alangomes.springspigot.context.Context;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

@Subcommand
@Command(
        name = "search",
        versionProvider = PluginVersionProvider.class,
        mixinStandardHelpOptions = true,
        description = "Command to list the available heads from mineskin in chat (for test purposes)"
)
@Slf4j
public class DbTestCommand extends AbstractCommand {

    private final HeadCreator headCreator;
    private final HeadPlacer headPlacer;
    private final NBTPrinter nbtPrinter;
    private final HeadRepository headRepository;
    private final HeadEntityToHeadMapper headEntityToHeadMapper;
    private final HeadSearcher headSearcher;

    @Parameters(
            index = "0",
            description = "The db action that needs to be done",
            paramLabel = "searchTerm"
    )
    private String searchTerm;

    public DbTestCommand(Context context,
                         ChatManager chatManager,
                         HeadCreator headCreator,
                         HeadPlacer headPlacer,
                         NBTPrinter nbtPrinter,
                         HeadRepository headRepository,
                         HeadEntityToHeadMapper headEntityToHeadMapper,
                         HeadSearcher headSearcher) {
        super(context, chatManager);
        this.headCreator = headCreator;
        this.headPlacer = headPlacer;
        this.nbtPrinter = nbtPrinter;
        this.headRepository = headRepository;
        this.headEntityToHeadMapper = headEntityToHeadMapper;
        this.headSearcher = headSearcher;
    }


    @Override
    public void run() {
        if (context.getPlayer() == null) {
            context.getSender().sendMessage(chatManager.getConsolePrefix() + "This command is only available for players.");
        }

        Optional<Head> head = headSearcher.getHeads(UUID.fromString(searchTerm));
        if (head.isPresent()) {
            showInfo(head.get());
        } else {
            context.getPlayer().sendMessage("Head for UUID " + searchTerm + " not found");
        }
        //heads.forEach(this::showInfo);

        //context.getPlayer().sendMessage("Total number of heads: " + heads.size());
    }

    private void showInfo(Head head) {
        context.getPlayer().sendMessage("   - " + head.getName());
        context.getPlayer().sendMessage("      db: " + head.getHeadDatabase());
        context.getPlayer().sendMessage("      owner: " + head.getHeadOwner());
        context.getPlayer().sendMessage("      value: " + new String(Base64.decodeBase64(head.getValue()), StandardCharsets.UTF_8));
    }
}