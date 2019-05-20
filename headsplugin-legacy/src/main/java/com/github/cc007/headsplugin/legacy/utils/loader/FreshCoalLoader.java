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
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
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
public class FreshCoalLoader implements DatabaseLoader
{

	@Override
	public List<Head> getHeads(String urlString, String searchTerm) throws MalformedURLException, SocketTimeoutException, IOException
	{
		JsonArray json;
		String all = URLReader.readUrl(urlString + searchTerm, "application/x-www-form-urlencoded", "POST");
		if (all == null) {
			throw new UnknownHostException("The website returns an unknown format. The url has probably been incorrectly set. Url string: " + urlString + searchTerm);
		}
		try {
			String body = all.substring(all.indexOf("<body>") + 6, all.indexOf("</body>"));
			body = body.trim();
			JsonParser jsonParser = new JsonParser();
			json = jsonParser.parse(body).getAsJsonArray();
		}
		catch (StringIndexOutOfBoundsException | JsonSyntaxException ex) {
			throw new UnknownHostException("The website returns an unknown format. The url has probably been incorrectly set. Url string: " + urlString + searchTerm);
		}
		// now turn the JsonArray into a list of heads
		List<Head> heads = new ArrayList<>();
		for (int i = 0; i < json.size(); i++) {
			String name = json.get(i).getAsJsonObject().getAsJsonPrimitive("name").getAsString();
			UUID skullOwner = UUID.fromString(json.get(i).getAsJsonObject().getAsJsonPrimitive("skullowner").getAsString());
			String value = json.get(i).getAsJsonObject().getAsJsonPrimitive("value").getAsString();
			heads.add(new Head(name, value, skullOwner));
		}
		return heads;
	}

	@Override
	public Head addHead(String urlString, UUID playerUuid, String name)
	{
		return null;
	}

	@Override
	public String getCategoriesUrl()
	{
		return HeadsPlugin.getHeadsPlugin().getConfig().getString("freshcoal.predefinedcategoriesurl");
	}

	@Override
	public String getSearchUrl()
	{
		return HeadsPlugin.getHeadsPlugin().getConfig().getString("freshcoal.customcategoriesurl");
	}

	@Override
	public String getGenerateUrl()
	{
		return null;
	}

}
