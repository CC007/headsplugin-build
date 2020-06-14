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
package com.github.cc007.headsplugin.legacy.utils.loader;

import com.github.cc007.headsplugin.legacy.HeadsPlugin;
import com.github.cc007.headsplugin.legacy.utils.URLReader;
import com.github.cc007.headsplugin.legacy.utils.heads.Head;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import org.apache.commons.codec.binary.Base64;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Rik Schaaf aka CC007 (http://coolcat007.nl/)
 */
public class MineSkinLoader implements DatabaseLoader {

    @Override
    public List<Head> getHeads(String urlString, String searchTerm) throws MalformedURLException, SocketTimeoutException, IOException {
        List<Head> heads = new ArrayList<>();

        String jsonString = URLReader.readUrl(urlString + "list?filter=" + searchTerm, "application/json", "GET");
        if (jsonString == null) {
            throw new UnknownHostException("The website returns an unknown format. The url has probably been incorrectly set. Url string: " + urlString + "list?filter=" + searchTerm);
        }

        int id = -1;
        try {
            JsonParser jsonParser = new JsonParser();
            JsonArray listJson = jsonParser.parse(jsonString).getAsJsonObject().getAsJsonArray("skins");
            // now turn the JsonArray into a list of heads
            for (int i = 0; i < listJson.size(); i++) {
                id = listJson.get(i).getAsJsonObject().getAsJsonPrimitive("id").getAsInt();
                jsonString = URLReader.readUrl(urlString + "id/" + id, "application/json", "GET");
                if (jsonString == null) {
                    throw new UnknownHostException("The website returns an unknown format. The url has probably been incorrectly set. Url string: " + urlString + "id/" + searchTerm);
                }
                JsonObject headJson = jsonParser.parse(jsonString).getAsJsonObject();

                String name = headJson.getAsJsonPrimitive("name").getAsString();
                UUID skullOwner = UUID.fromString(headJson.getAsJsonObject("data").getAsJsonPrimitive("uuid").getAsString());

                String tempValue = headJson.getAsJsonObject("data").getAsJsonObject("texture").getAsJsonPrimitive("value").getAsString();
                String decodedValue = new String(Base64.decodeBase64(tempValue), "UTF-8");
                String strippedDecodedValue = "{\"textures" + decodedValue.split("textures", 2)[1];
                String value = Base64.encodeBase64String(strippedDecodedValue.getBytes("UTF-8"));
                heads.add(new Head(name, value, skullOwner));
            }

        } catch (JsonSyntaxException ex) {
            String errorMsg = "The website returns an unknown format. The url has probably been incorrectly set. \n  Url string for search: " + urlString + "list?filter=" + searchTerm;
            if (id != -1) {
                errorMsg += "\n  Url string for id fetch: " + urlString + "id/" + id;
            }
            throw new UnknownHostException(errorMsg);
        }
        return heads;
    }

    @Override
    public Head addHead(String urlString, UUID playerUuid, String headName) throws MalformedURLException, SocketTimeoutException, IOException {
        try {
            String jsonString = URLReader.readUrl(urlString + playerUuid.toString() + "?name=" + headName, "application/json", "GET");

            if (jsonString == null) {
                throw new UnknownHostException("The website returns an unknown format. The url has probably been incorrectly set. Url string: " + urlString + playerUuid.toString() + "?name=" + headName);
            }
            HeadsPlugin.getHeadsPlugin().getLogger().info("varable jsonString: " + jsonString);
            JsonParser jsonParser = new JsonParser();
            JsonObject headJson = jsonParser.parse(jsonString).getAsJsonObject();

            if (headJson.has("error")) {
                throw new IOException(headJson.getAsJsonPrimitive("error").getAsString());
            }
            String name = headJson.getAsJsonPrimitive("name").getAsString();
            UUID skullOwner = UUID.fromString(headJson.getAsJsonObject("data").getAsJsonPrimitive("uuid").getAsString());

            String tempValue = headJson.getAsJsonObject("data").getAsJsonObject("texture").getAsJsonPrimitive("value").getAsString();
            String decodedValue = new String(Base64.decodeBase64(tempValue), "UTF-8");
            String strippedDecodedValue = "{\"textures" + decodedValue.split("textures", 2)[1];
            String value = Base64.encodeBase64String(strippedDecodedValue.getBytes("UTF-8"));
            return new Head(name, value, skullOwner);
        } catch (JsonSyntaxException ex) {
            String errorMsg = "The website returns an unknown format. The url has probably been incorrectly set. \n  Url string for search: " + urlString + playerUuid.toString() + "?name=" + headName;
            throw new IOException(errorMsg);
        }
    }

    @Override
    public String getCategoriesUrl() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getSearchUrl() {
        return HeadsPlugin.getHeadsPlugin().getConfig().getString("mineskin.customcategoriesurl");
    }

    @Override
    public String getGenerateUrl() {
        return HeadsPlugin.getHeadsPlugin().getConfig().getString("mineskin.generateurl");
    }
}
