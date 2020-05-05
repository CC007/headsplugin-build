package com.github.cc007.headsplugin.presentation.commands.headsplugin;

import com.github.cc007.headsplugin.api.business.domain.Category;
import com.github.cc007.headsplugin.api.business.domain.Head;
import com.github.cc007.headsplugin.api.business.services.heads.CategorySearcher;
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
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.springframework.transaction.annotation.Transactional;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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
    private final HeadSearcher headSearcher;
    private final CategorySearcher categorySearcher;

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
                         HeadSearcher headSearcher,
                         CategorySearcher categorySearcher) {
        super(context, chatManager);
        this.headCreator = headCreator;
        this.headPlacer = headPlacer;
        this.nbtPrinter = nbtPrinter;
        this.headSearcher = headSearcher;
        this.categorySearcher = categorySearcher;
    }


    @Override
    public void run() {
        if (context.getPlayer() == null) {
            context.getSender().sendMessage(chatManager.getConsolePrefix() + "This command is only available for players.");
        }

        Set<Category> categories = categorySearcher.getCategories();
        context.getSender().sendMessage(chatManager.getChatPrefix() + "Available categories: " + categories.stream().map(Category::getName).collect(Collectors.joining(", ")));

        Location location = context.getPlayer().getLocation();
        for (Category category : categories) {
            Optional<Head> optionalHead = categorySearcher.getCategoryHeads(category).stream().findFirst();
            if(optionalHead.isPresent()){
                Head head = optionalHead.get();
                showInfo(head);
                ItemStack headIS = headCreator.getItemStack(head);
                context.getPlayer().getInventory().addItem(headIS);
                headPlacer.placeHead(headIS, location, BlockFace.NORTH);
            } else {
                context.getPlayer().sendMessage("First head for category " + category.getName() + " not found");
            }
            location = location.add(1, 0, 0);
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