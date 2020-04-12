package com.github.cc007.headsplugin.business.services.chat;

import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import dev.alangomes.springspigot.context.Context;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatManager {

    private final Context context;

    public String getPrefix() {
        if (context.getPlayer() != null) {
            getChatPrefix();
        } else if (context.getSender() instanceof ConsoleCommandSender) {
            getConsolePrefix();
        }
        return "";
    }

    public String getConsolePrefix() {
        return "[HeadsPluginAPI] ";
    }

    public String getChatPrefix() {
        return ChatColor.DARK_AQUA + "[" + ChatColor.GOLD + "Heads" + ChatColor.GREEN + "Plugin" + ChatColor.AQUA + "API" + ChatColor.DARK_AQUA + "]" + ChatColor.WHITE + " ";
    }

    public String getChatBanner() {
        return ChatColor.DARK_AQUA + "          ----- " + ChatColor.GOLD + "Heads" + ChatColor.GREEN + "Plugin" + ChatColor.AQUA + "API" + ChatColor.DARK_AQUA + " -----" + ChatColor.WHITE + "\n";
    }
}
