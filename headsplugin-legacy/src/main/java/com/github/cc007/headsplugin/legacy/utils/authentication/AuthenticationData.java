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
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.UUID;
import java.util.logging.Level;

/**
 * @author Rik Schaaf aka CC007 (http://coolcat007.nl/)
 */
public class AuthenticationData {

    private UUID player;

    private AccessMode accessMode;

    /**
     * Create a new authentication data object from the data string
     *
     * @param dataString the string containing the data that was retrieved from the server
     */
    public AuthenticationData(String dataString) {
        JsonParser parser = new JsonParser();
        JsonObject serverData = (JsonObject) parser.parse(dataString);
        HeadsPlugin.getHeadsPlugin().getLogger().log(Level.INFO, "Player: " + serverData.get("player").getAsString());
        HeadsPlugin.getHeadsPlugin().getLogger().log(Level.INFO, "Access mode: " + serverData.get("accessMode").getAsString());
        player = UUID.fromString(serverData.get("player").getAsString());
        switch (serverData.get("accessMode").getAsString().toLowerCase()) {
            case "full":
                accessMode = AccessMode.FULL;
                break;
            case "lite":
                accessMode = AccessMode.LITE;
                break;
            case "expired":
                accessMode = AccessMode.EXPIRED;
                break;
//			case "none":
//				accessMode = AccessMode.NONE;
//				break;
            default:
                accessMode = AccessMode.NONE;
        }
    }

    /**
     * Get the value of player
     *
     * @return the value of player
     */
    public UUID getPlayer() {
        return player;
    }

    /**
     * Set the value of player
     *
     * @param player new value of player
     */
    public void setPlayer(UUID player) {
        this.player = player;
    }

    /**
     * Get the value of accessMode
     *
     * @return the value of accessMode
     */
    public AccessMode getAccessMode() {
        return accessMode;
    }

    /**
     * Set the value of accessMode
     *
     * @param accessMode new value of accessMode
     */
    public void setAccessMode(AccessMode accessMode) {
        this.accessMode = accessMode;
    }
}
