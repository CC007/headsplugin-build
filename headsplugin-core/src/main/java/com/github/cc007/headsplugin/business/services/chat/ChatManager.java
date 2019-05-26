package com.github.cc007.headsplugin.business.services.chat;

import org.bukkit.ChatColor;
import org.springframework.stereotype.Component;

@Component
public class ChatManager
{

	public String getConsolePrefix()
	{
		return "[HeadsPluginAPI] ";
	}

	public String getChatPrefix()
	{
		return ChatColor.DARK_AQUA + "[" + ChatColor.GOLD + "Heads" + ChatColor.GREEN + "Plugin" + ChatColor.AQUA + "API" + ChatColor.DARK_AQUA + "]" + ChatColor.WHITE + " ";
	}

	public String getChatBanner(){
		return ChatColor.DARK_AQUA + "          ----- " + ChatColor.GOLD + "Heads" + ChatColor.GREEN + "Plugin" + ChatColor.AQUA + "API" + ChatColor.DARK_AQUA + " -----" + ChatColor.WHITE + "\n";
	}
}
