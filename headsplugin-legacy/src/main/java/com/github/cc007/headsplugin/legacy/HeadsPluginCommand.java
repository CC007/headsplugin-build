/*
 * The MIT License
 *
 * Copyright 2015 Rik Schaaf aka CC007 (http://coolcat007.nl/).
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
package com.github.cc007.headsplugin.legacy;

import com.github.cc007.headsplugin.legacy.utils.authentication.KeyAuthenticator;

import org.apache.http.conn.ConnectTimeoutException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.logging.Level;

/**
 * @author Rik Schaaf aka CC007 (http://coolcat007.nl/)
 */
public class HeadsPluginCommand implements CommandExecutor {

    private final HeadsPlugin plugin;

    public HeadsPluginCommand(HeadsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if (!sender.hasPermission("headsplugin.register")) {
            sender.sendMessage("You don't have permission to perform any commands the plugin.");
            return false;
        }
        sender.sendMessage("At the moment this plugin doesn't have any commands");
        return true;
        
        /*if (args.length <= 0) {
            sender.sendMessage("Incorrect use of the command. Correct usage: \n /headsplugin verify <accountName>\n /headsplugin register <accountName> <serverName> <token>\n /headsplugin authenticate\n /headsplugin setkey <key>\n /headsplugin setworld <worldname>");
            return false;
        }

        switch (args[0].toLowerCase()) {
            case "verify":
                return onVerifyCommand(sender, command, commandLabel, args);
            case "register":
                return onRegisterCommand(sender, command, commandLabel, args);
            case "authenticate":
                plugin.authenticate();
                sender.sendMessage("Reauthentication complete.");
                switch (plugin.getAccessMode()) {
                    case FULL:
                        sender.sendMessage("Server authenticated for full use (all functionalities enabled).");
                        break;
                    case LITE:
                        sender.sendMessage("Server authenticated for lite use (only lite functionalities enabled).");
                        break;
                    case EXPIRED:
                        sender.sendMessage("Expired authentication found. Server authenticated for lite use (only lite functionalities enabled).");
                        break;
                    case NONE:
                        sender.sendMessage("Could not authenticate user. Server authenticated for lite use (only lite functionalities enabled).");
                        break;
                }
                return true;
            case "setkey":
                return onSetKeyCommand(sender, command, commandLabel, args);
            case "setworld":
                return onSetWorldCommand(sender, command, commandLabel, args);
        }
        sender.sendMessage("Incorrect use of the command. Correct usage: \n /headsplugin verify <accountName>\n /headsplugin register <accountName> <serverName> <token>\n /headsplugin authenticate\n /headsplugin setkey <key>\n /headsplugin setworld <worldname>");
        return false;
        */
    }

    private boolean onVerifyCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            plugin.getLogger().log(Level.SEVERE, "Only players can perform this command.");
            return false;
        }

        if (args.length < 2) {
            sender.sendMessage("You didn't provide an account name. Correct usage: /headsplugin verify <accountName>");
            return false;
        }

        try {
            if (KeyAuthenticator.verify((Player) sender, args[1])) {
                plugin.getConfig().set("accountname", args[1]);
                plugin.saveConfig();
                sender.sendMessage("The account is verified");
            } else {
                sender.sendMessage("The account is not verified or was already verified. Did you provide the right account name?");
            }
        } catch (ConnectTimeoutException | SocketTimeoutException | ConnectException ex) {
            sender.sendMessage("Unable to connect to the heads plugin server");
        }
        return true;
    }

    private boolean onRegisterCommand(CommandSender sender, Command command, String commandLabel, String[] args) {

        if (args.length < 2) {
            sender.sendMessage("You didn't provide an account name. Correct usage: /headsplugin register <accountName> <serverName> <token>");
            return false;
        }
        if (args.length < 3) {
            sender.sendMessage("You didn't provide a server name. Correct usage: /headsplugin register <accountName> <serverName> <token>");
            return false;
        }
        if (args.length < 4) {
            sender.sendMessage("You didn't provide a registration token. Correct usage: /headsplugin register <accountName> <serverName> <token>");
            return false;
        }

        try {
            if (KeyAuthenticator.register(args[1], args[2], args[3])) {
                plugin.getConfig().set("accountname", args[1]);
                plugin.getConfig().set("servername", args[2]);
                plugin.saveConfig();
                sender.sendMessage("The server is registered");
            } else {
                sender.sendMessage("The server is not registered or was already registered. Did you provide the right user name, server name and token?");
            }
        } catch (ConnectTimeoutException | SocketTimeoutException | ConnectException ex) {
            sender.sendMessage("Unable to connect to the heads plugin server");
        }
        return true;
    }

    private boolean onSetKeyCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if (args.length <= 1) {
            sender.sendMessage("You didn't specify a key. Correct usage: /headsplugin setkey <key>");
            return false;
        }
        plugin.getConfig().set("authenticationkey", args[1]);
        plugin.saveConfig();
        sender.sendMessage("The new key is set");
        return true;
    }

    private boolean onSetWorldCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if (args.length <= 1) {
            sender.sendMessage("You didn't specify a world name. Correct usage: /headsplugin setworld <worldname>");
            return false;
        }
        plugin.getConfig().set("world", args[1]);
        plugin.saveConfig();
        sender.sendMessage("The new world name is set");
        return true;
    }
}
