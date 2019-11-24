package com.github.cc007.headsplugin.presentation.commands.headsplugin;

import com.github.cc007.headsplugin.business.domain.Head;
import com.github.cc007.headsplugin.business.services.NBTPrinter;
import com.github.cc007.headsplugin.business.services.chat.ChatManager;
import com.github.cc007.headsplugin.business.services.heads.HeadCreator;
import com.github.cc007.headsplugin.business.services.heads.HeadPlacer;
import com.github.cc007.headsplugin.config.PluginVersionProvider;
import com.github.cc007.headsplugin.integration.daos.heads.MinecraftHeadsDao;
import com.github.cc007.headsplugin.presentation.commands.AbstractCommand;
import de.tr7zw.nbtapi.NBTTileEntity;
import dev.alangomes.springspigot.command.Subcommand;
import dev.alangomes.springspigot.context.Context;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.codec.binary.Base64;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Subcommand
@Command(
        name = "list",
        versionProvider = PluginVersionProvider.class,
        mixinStandardHelpOptions = true,
        description = "Command to list the available heads from mineskin in chat (for test purposes)",
        aliases = {"lst"}
)
@Slf4j
public class MineSkinApiTestCommand extends AbstractCommand {

    private final MinecraftHeadsDao minecraftHeadsDao;
    private final HeadCreator headCreator;
    private final HeadPlacer headPlacer;
    private final NBTPrinter nbtPrinter;

    @Parameters(
            index = "0",
            description = "The search term to find the heads",
            paramLabel = "searchTerm"
    )
    private int searchTerm;

    public MineSkinApiTestCommand(Context context,
                                  ChatManager chatManager,
                                  MinecraftHeadsDao minecraftHeadsDao,
                                  HeadCreator headCreator,
                                  HeadPlacer headPlacer,
                                  NBTPrinter nbtPrinter) {
        super(context, chatManager);
        this.minecraftHeadsDao = minecraftHeadsDao;
        this.headCreator = headCreator;
        this.headPlacer = headPlacer;
        this.nbtPrinter = nbtPrinter;
    }


    @Override
    public void run() {
        if (context.getPlayer() == null) {
            context.getSender().sendMessage(chatManager.getConsolePrefix() + "This command is only available for players.");
        }
        val heads = minecraftHeadsDao.getCategoryHeads(minecraftHeadsDao.getCategoryNames().get(0));

        context.getPlayer().getInventory().addItem(headCreator.getItemStack(heads.get(searchTerm)));

        //showInfo(heads);
        val location = context.getPlayer().getLocation();
        location.add(0, -1, 0);
        Block headBlock = location.getBlock();
        try {
            nbtPrinter.printNBTTileEntity(new NBTTileEntity(headBlock.getState()));
        } catch (Exception e) {
            log.error("Error with printing the NBT for the block below the player" + e);
        }

        headPlacer.placeHead(heads.get(searchTerm), location, BlockFace.NORTH);
    }

    private void showInfo(List<Head> heads) {
        context.getPlayer().sendMessage(chatManager.getChatPrefix() + "The following heads are available:");
        for (Head head : heads) {
            context.getPlayer().sendMessage("   - " + head.getName());
            context.getPlayer().sendMessage("      db: " + head.getHeadDatabase());
            context.getPlayer().sendMessage("      owner: " + head.getHeadOwner());
            context.getPlayer().sendMessage("      value: " + new String(Base64.decodeBase64(head.getValue()), StandardCharsets.UTF_8));
        }
    }
}