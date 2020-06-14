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
package com.github.cc007.headsplugin.legacy.utils.authentication;

import com.github.cc007.headsplugin.legacy.HeadsPlugin;
import com.github.cc007.headsplugin.legacy.utils.REST;

import org.apache.http.conn.ConnectTimeoutException;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

/**
 * @author Rik Schaaf aka CC007 (http://coolcat007.nl/)
 */
public class KeyAuthenticator {

    //private static final String host = "https://headsplugin.coolcat007.nl";
    //private static final String restPath = "headsplugin/api";
    private static final String host = "https://headsplugin-coolcat007.rhcloud.com";
    private static final String restPath = "api";
    private static final String requestRestEndpoint = "authenticate";
    private static final String registerRestEndpoint = "register";
    private static final String verifyRestEndpoint = "verify";

    public static AccessMode getAccessMode(String key) {
        try {
            AuthenticationData data = retrieveServerData(key);
            if (data != null && checkOperator(data.getPlayer())) {
                return data.getAccessMode();
            }
        } catch (ConnectTimeoutException | SocketTimeoutException | ConnectException ex) {
            try {
                AuthenticationData data = retrieveServerData(key);
                if (data != null && checkOperator(data.getPlayer())) {
                    return data.getAccessMode();
                }
            } catch (ConnectTimeoutException | SocketTimeoutException | ConnectException ex2) {
                HeadsPlugin.getHeadsPlugin().getLogger().log(Level.WARNING, "Can not reach the headsplugin website. Please retry authentication later: '/headsplugin authenticate'.");
            }
        }
        return AccessMode.NONE;
    }

    private static boolean checkOperator(UUID player) {
        Set<OfflinePlayer> ops = Bukkit.getOperators();
        for (OfflinePlayer op : ops) {
            if (op.getUniqueId().equals(player)) {
                return true;
            }
        }
        return false;
    }

    private static AuthenticationData retrieveServerData(String key) throws ConnectTimeoutException, SocketTimeoutException, ConnectException {
        String dataString = retrieveDataString(key);
        if ("".equals(dataString)) {
            return null;
        }
        return new AuthenticationData(dataString);
    }

    private static String retrieveDataString(String key) throws ConnectTimeoutException, SocketTimeoutException, ConnectException {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            HeadsPlugin plugin = HeadsPlugin.getHeadsPlugin();
            FileConfiguration fc = plugin.getConfig();
            String worldName = fc.getString("world");
            if (worldName == null) {
                HeadsPlugin.getHeadsPlugin().getLogger().warning("Corrupt config file: the config file doesn't contain a world element.");
                return "";
            }
            World w = Bukkit.getWorld(worldName);
            if (w == null) {
                HeadsPlugin.getHeadsPlugin().getLogger().warning("The given world in the config file doesn't exist.");
                return "";
            }
            UUID worldUID = w.getUID();
            if (worldUID == null) {
                HeadsPlugin.getHeadsPlugin().getLogger().warning("Couldn't find the UUID for the given world in the config file.");
                return "";
            }
            String uidString = worldUID.toString();
            md.update(uidString.getBytes("UTF-8"));
            md.update(key.getBytes("UTF-8"));
            byte[] digest = md.digest();
            return requestDataString(new BigInteger(1, digest).toString(16));
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            HeadsPlugin.getHeadsPlugin().getLogger().log(Level.SEVERE, null, ex);
        }
        return "";
    }

    private static String requestDataString(String keyHash) throws ConnectTimeoutException, SocketTimeoutException, ConnectException {
        Map<String, String> params = new HashMap<>();
        params.put("hash", keyHash);
        params.put("accountName", HeadsPlugin.getHeadsPlugin().getConfig().getString("accountname"));
        params.put("serverName", HeadsPlugin.getHeadsPlugin().getConfig().getString("servername"));
        return REST.get(host, restPath, requestRestEndpoint, params, true);
    }

    public static boolean register(String accountName, String serverName, String token) throws ConnectTimeoutException, SocketTimeoutException, ConnectException {
        Map<String, String> params = new HashMap<>();
        try {
            String worldName = HeadsPlugin.getHeadsPlugin().getConfig().getString("world");
            String worldUID = Bukkit.getWorld(worldName).getUID().toString();
            String encoded = DatatypeConverter.printBase64Binary(encrypt(worldUID, token).getBytes("UTF-8"));
            params.put("id", encoded);
            params.put("accountName", accountName);
            params.put("serverName", serverName);
        } catch (UnsupportedEncodingException ex) {
            HeadsPlugin.getHeadsPlugin().getLogger().log(Level.SEVERE, null, ex);
        }
        return new RegistrationData(REST.get(host, restPath, registerRestEndpoint, params, true)).isRegistered();
    }

    public static boolean verify(Player player, String accountName) throws ConnectTimeoutException, SocketTimeoutException, ConnectException {
        Map<String, String> params = new HashMap<>();
        try {
            String accountUID = player.getUniqueId().toString();
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(accountName.getBytes("UTF-8"));
            md.update(accountUID.getBytes("UTF-8"));
            byte[] digest = md.digest();
            params.put("id", new BigInteger(1, digest).toString(16));
            params.put("accountName", accountName);
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException ex) {
            HeadsPlugin.getHeadsPlugin().getLogger().log(Level.SEVERE, null, ex);
        }
        return new VerificationData(REST.get(host, restPath, verifyRestEndpoint, params, true)).isVerified();
    }

    private static String encrypt(String data, String key) {
        try {
            byte[] result = new byte[data.length()];
            byte[] dataBytes = data.getBytes("UTF-8");
            byte[] keyBytes = key.getBytes("UTF-8");
            for (int i = 0; i < data.getBytes().length; i++) {
                result[i] = (byte) (dataBytes[i] ^ keyBytes[i % key.length()]);
            }
            return new String(result, Charset.forName("UTF-8"));
        } catch (UnsupportedEncodingException | UnsupportedCharsetException ex) {
            HeadsPlugin.getHeadsPlugin().getLogger().log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
