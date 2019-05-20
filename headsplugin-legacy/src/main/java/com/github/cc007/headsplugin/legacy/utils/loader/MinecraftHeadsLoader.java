/*
 * The MIT License
 *
 * Copyright 2018 Rik Schaaf aka CC007 (http://coolcat007.nl/).
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
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.command.CommandSender;

/**
 * @author Rik Schaaf aka CC007 (http://coolcat007.nl/)
 */
public class MinecraftHeadsLoader implements DatabaseLoader
{

	CommandSender sender = null;

	public MinecraftHeadsLoader(CommandSender sender)
	{
		this.sender = sender;
	}

	@Override
	public List<Head> getHeads(String urlString, String searchTerm) throws MalformedURLException, SocketTimeoutException, IOException
	{
		JsonArray json;
		String jsonString = URLReader.readUrl(urlString + searchTerm, "application/json", "GET");
		if (jsonString == null) {
			throw new UnknownHostException("The website returns an unknown format. The url has probably been incorrectly set. Url string: " + urlString + searchTerm);
		}
		try {
			JsonParser jsonParser = new JsonParser();
			json = jsonParser.parse(jsonString).getAsJsonArray();
		}
		catch (StringIndexOutOfBoundsException | JsonSyntaxException ex) {
			throw new UnknownHostException("The website returns an unknown format. The url has probably been incorrectly set. Url string: " + urlString + searchTerm);
		}
		// now turn the JsonArray into a list of heads
		List<Head> heads = new ArrayList<>();
		for (int i = 0; i < json.size(); i++) {
			String name = json.get(i).getAsJsonObject().getAsJsonPrimitive("name").getAsString();
			UUID skullOwner = UUID.fromString(json.get(i).getAsJsonObject().getAsJsonPrimitive("uuid").getAsString().trim());
			String value = json.get(i).getAsJsonObject().getAsJsonPrimitive("value").getAsString();
			heads.add(new Head(name, value, skullOwner));
		}
		return heads;
	}


	@Override
	public Head addHead(String urlString, UUID playerUuid, String headName)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public String getCategoriesUrl()
	{
		return HeadsPlugin.getHeadsPlugin().getConfig().getString("minecraftheads.predefinedcategoriesurl");
	}

	@Override
	public String getSearchUrl()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public String getGenerateUrl()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

}
