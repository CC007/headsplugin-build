package com.github.cc007.headsplugin.handlers;

import com.github.cc007.headsplugin.api.business.domain.events.CategoryUpdatedEvent;
import com.github.cc007.headsplugin.config.properties.CategoriesProperties;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

@RequiredArgsConstructor
public class CategoryUpdatedEventListener implements Listener {

    private final CategoriesProperties categoriesProperties;

    @EventHandler
    public void onCategoryUpdated(CategoryUpdatedEvent event) {
        // Get the list of players to notify from the config.yml file
        List<String> playersToNotify = categoriesProperties.getUpdate().getNotifyEach();

        // Send a message to each player
        for (String playerName : playersToNotify) {
            Player player = Bukkit.getServer().getPlayer(playerName);
            if (player != null && player.isOnline()) {
                player.sendMessage("Category " + event.getCategoryName() + " has been updated in " + event.getDuration() + " seconds.");
            }
        }
    }
}
