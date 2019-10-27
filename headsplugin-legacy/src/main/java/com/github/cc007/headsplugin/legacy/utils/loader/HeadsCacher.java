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
package com.github.cc007.headsplugin.legacy.utils.loader;

import com.github.cc007.headsplugin.legacy.HeadsPlugin;
import com.github.cc007.headsplugin.legacy.utils.heads.Head;
import com.github.cc007.headsplugin.legacy.utils.heads.HeadsCategory;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.IOUtils;
import org.bukkit.Bukkit;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

/**
 * @author Rik Schaaf aka CC007 (http://coolcat007.nl/)
 */
public class HeadsCacher {

    public static void cacheCategory(HeadsCategory category, File dataFolder) {
        String categoryName = category.getCategoryName();
        if (categoryName.equalsIgnoreCase("everything")) {
            return;
        }

        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }
        File dir = new File(dataFolder, "cache");
        if (!dir.exists()) {
            dir.mkdir();
        }
        File file = new File(dataFolder + "/cache/", categoryName + ".json");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                Bukkit.getLogger().log(Level.SEVERE, "Couldn''t create {0}.json", categoryName);
                try {
                    Bukkit.getLogger().log(Level.SEVERE, "File path: {0}", file.getCanonicalPath());
                } catch (IOException ex) {
                    Bukkit.getLogger().log(Level.SEVERE, null, ex);
                }
            }
        }
        String jsonString = generateJsonString(category);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
            writer.write(jsonString);
            writer.flush();
            HeadsPlugin.getHeadsPlugin().getLogger().info("Done updating " + categoryName + ".json");
        } catch (IOException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Couldn''t write to {0}.json", categoryName);
        }
    }

    private static String generateJsonString(HeadsCategory category) {
        Gson gson = new Gson();
        JsonArray heads = new JsonArray();
        for (Head list : category.getList()) {
            JsonObject head = new JsonObject();
            head.addProperty("name", list.getName());
            head.addProperty("value", list.getValue());
            head.addProperty("skullowner", list.getHeadOwner().toString());
            heads.add(head);
        }
        return gson.toJson(heads);
    }

    public static boolean isCategoryRecentlyCached(HeadsCategory category, File dataFolder) {
        long x = 1; //nr of days
        File file = new File(dataFolder + "/cache/", category.getCategoryName() + ".json");
        if (file.exists()) {
            long diff = new Date().getTime() - file.lastModified();
            return diff < x * 24 * 60 * 60 * 1000;
        }
        return false;
    }

    public static List<Head> getHeads(String categoryName, File dataFolder) throws IOException {
        Bukkit.getLogger().log(Level.WARNING, "Could not get category {0}: unable to connect to the website, using cache instead.", categoryName);
        File categoryFile = new File(dataFolder + "/cache/", categoryName + ".json");
        JsonArray json;
        if (categoryFile.exists()) {
            json = new JsonParser().parse(IOUtils.toString(new FileInputStream(categoryFile))).getAsJsonArray();
        } else {
            Bukkit.getLogger().log(Level.SEVERE, "No cache was found for category {0}!", categoryName);
            json = new JsonArray();
        }
        List<Head> heads = new ArrayList<>();
        for (int i = 0; i < json.size(); i++) {
            String name = json.get(i).getAsJsonObject().getAsJsonPrimitive("name").getAsString();
            UUID skullOwner = UUID.fromString(json.get(i).getAsJsonObject().getAsJsonPrimitive("skullowner").getAsString());
            String value = json.get(i).getAsJsonObject().getAsJsonPrimitive("value").getAsString();
            heads.add(new Head(name, value, skullOwner));
        }
        return heads;
    }
}
