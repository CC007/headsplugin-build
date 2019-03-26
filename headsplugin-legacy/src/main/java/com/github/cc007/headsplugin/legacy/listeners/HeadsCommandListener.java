/*
 * The MIT License
 *
 * Copyright 2017 Rik Schaaf aka CC007 (http://coolcat007.nl/).
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.github.cc007.headsplugin.legacy.listeners;

import com.github.cc007.headsplugin.legacy.HeadsPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/**
 *
 * @author Rik Schaaf aka CC007 (http://coolcat007.nl/)
 */
public class HeadsCommandListener implements Listener{
    @EventHandler(priority = EventPriority.LOW)
    public void onCommandPreProcess(PlayerCommandPreprocessEvent event) {
        CommandSender sender = event.getPlayer();
        String[] cmd = event.getMessage().split(" ");
        if(cmd[0].equals("/heads")) {
            if(HeadsPlugin.getHeadsPlugin().getPlugin("HeadsInventory") == null) {
                sender.sendMessage(HeadsPlugin.pluginChatPrefix(true) + "This command is only available if you have installed both HeadsPlugin and HeadsInventory.");
                event.setCancelled(true);
            }
        }
    }
}
