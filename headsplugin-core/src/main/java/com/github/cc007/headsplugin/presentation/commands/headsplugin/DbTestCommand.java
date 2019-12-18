package com.github.cc007.headsplugin.presentation.commands.headsplugin;

import com.github.cc007.headsplugin.business.domain.Head;
import com.github.cc007.headsplugin.business.services.NBTPrinter;
import com.github.cc007.headsplugin.business.services.chat.ChatManager;
import com.github.cc007.headsplugin.business.services.heads.HeadCreator;
import com.github.cc007.headsplugin.business.services.heads.HeadPlacer;
import com.github.cc007.headsplugin.config.PluginVersionProvider;
import com.github.cc007.headsplugin.integration.database.mappers.domain.HeadToHeadEntityMapper;
import com.github.cc007.headsplugin.integration.database.mappers.entity.HeadEntityToHeadMapper;
import com.github.cc007.headsplugin.integration.database.repositories.HeadRepository;
import com.github.cc007.headsplugin.integration.rest.daos.heads.MinecraftHeadsDao;
import com.github.cc007.headsplugin.presentation.commands.AbstractCommand;

import dev.alangomes.springspigot.command.Subcommand;
import dev.alangomes.springspigot.context.Context;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.codec.binary.Base64;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.nio.charset.StandardCharsets;
import java.util.stream.StreamSupport;

@Subcommand
@Command(
        name = "do",
        versionProvider = PluginVersionProvider.class,
        mixinStandardHelpOptions = true,
        description = "Command to list the available heads from mineskin in chat (for test purposes)"
)
@Slf4j
public class DbTestCommand extends AbstractCommand {

    private final MinecraftHeadsDao minecraftHeadsDao;
    private final HeadCreator headCreator;
    private final HeadPlacer headPlacer;
    private final NBTPrinter nbtPrinter;
    private final HeadRepository headRepository;
    private final HeadToHeadEntityMapper headToHeadEntityMapper;
    private final HeadEntityToHeadMapper headEntityToHeadMapper;

    @Parameters(
            index = "0",
            description = "The db action that needs to be done",
            paramLabel = "action"
    )
    private String action;

    public DbTestCommand(Context context,
                         ChatManager chatManager,
                         MinecraftHeadsDao minecraftHeadsDao,
                         HeadCreator headCreator,
                         HeadPlacer headPlacer,
                         NBTPrinter nbtPrinter,
                         HeadRepository headRepository,
                         HeadToHeadEntityMapper headToHeadEntityMapper,
                         HeadEntityToHeadMapper headEntityToHeadMapper) {
        super(context, chatManager);
        this.minecraftHeadsDao = minecraftHeadsDao;
        this.headCreator = headCreator;
        this.headPlacer = headPlacer;
        this.nbtPrinter = nbtPrinter;
        this.headRepository = headRepository;
        this.headToHeadEntityMapper = headToHeadEntityMapper;
        this.headEntityToHeadMapper = headEntityToHeadMapper;
    }


    @Override
    public void run() {
        if (context.getPlayer() == null) {
            context.getSender().sendMessage(chatManager.getConsolePrefix() + "This command is only available for players.");
        }

        switch (action) {
            case "save":
                val heads = minecraftHeadsDao.getCategoryHeads(minecraftHeadsDao.getCategoryNames().get(0));
                heads.parallelStream()
                        .map(headToHeadEntityMapper::transform)
                        .forEach((headRepository::save));
                break;
            case "load":
                StreamSupport.stream(headRepository.findAll().spliterator(), false)
                        .map(headEntityToHeadMapper::transform)
                        .forEach(this::showInfo);
                break;
        }
    }

    private void showInfo(Head head) {
        context.getPlayer().sendMessage("   - " + head.getName());
        context.getPlayer().sendMessage("      db: " + head.getHeadDatabase());
        context.getPlayer().sendMessage("      owner: " + head.getHeadOwner());
        context.getPlayer().sendMessage("      value: " + new String(Base64.decodeBase64(head.getValue()), StandardCharsets.UTF_8));
    }
}