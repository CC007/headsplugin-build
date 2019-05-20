/*
 * The MIT License
 *
 * Copyright 2015 Rik Schaaf aka CC007 <http://coolcat007.nl/>.
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
import com.github.cc007.headsplugin.legacy.exceptions.AuthenticationException;
import com.github.cc007.headsplugin.legacy.utils.heads.Head;
import com.github.cc007.headsplugin.legacy.utils.heads.HeadsCategories;
import com.github.cc007.headsplugin.legacy.utils.heads.HeadsCategory;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

/**
 * @author Rik Schaaf aka CC007 (http://coolcat007.nl/)
 */
public class HeadsLoader
{

	//****** Loading all categories ******//
	public static void loadCategories(HeadsCategories categories) throws MalformedURLException, IOException, AuthenticationException, UnsupportedOperationException
	{
		loadPredefinedCategories(categories);
		loadCustomCategories(categories);
	}

	public static void loadPredefinedCategories(HeadsCategories categories) throws MalformedURLException, IOException, UnsupportedOperationException
	{
		Set<String> categoryNames = HeadsPlugin.getHeadsPlugin().getCategoriesConfig().getConfigurationSection("predefinedcategories").getKeys(false);
		for (String categoryName : categoryNames) {
			loadPredefinedCategory(categories, categoryName);
		}
	}

	public static void loadPredefinedCategories(HeadsCategories categories, String url, DatabaseLoader loader) throws MalformedURLException, IOException
	{
		Set<String> categoryNames = HeadsPlugin.getHeadsPlugin().getCategoriesConfig().getConfigurationSection("predefinedcategories").getKeys(false);
		for (String categoryName : categoryNames) {
			loadPredefinedCategory(categories, url, categoryName, loader);
		}
	}

	public static void loadCustomCategories(HeadsCategories categories) throws MalformedURLException, IOException, AuthenticationException, UnsupportedOperationException
	{
		Set<String> categoryNames = HeadsPlugin.getHeadsPlugin().getCategoriesConfig().getConfigurationSection("customcategories").getKeys(false);
		for (String categoryName : categoryNames) {
			loadCustomCategory(categories, categoryName);
		}
	}

	public static void loadCustomCategories(HeadsCategories categories, String url, DatabaseLoader loader) throws MalformedURLException, IOException, AuthenticationException
	{
		Set<String> categoryNames = HeadsPlugin.getHeadsPlugin().getCategoriesConfig().getConfigurationSection("customcategories").getKeys(false);
		for (String categoryName : categoryNames) {
			loadCustomCategory(categories, url, categoryName, loader);
		}
	}

	//****** Loading a specific category ******//
	public static void loadCategory(HeadsCategories categories, String categoryName) throws MalformedURLException, IOException, AuthenticationException, UnsupportedOperationException
	{
		Set<String> predefinedCategoryNames = HeadsPlugin.getHeadsPlugin().getCategoriesConfig().getConfigurationSection("predefinedcategories").getKeys(false);
		Set<String> customCategoryNames = HeadsPlugin.getHeadsPlugin().getCategoriesConfig().getConfigurationSection("customcategories").getKeys(false);

		if (predefinedCategoryNames.contains(categoryName.toLowerCase())) {
			loadPredefinedCategory(categories, categoryName.toLowerCase());
		}
		else if (customCategoryNames.contains(categoryName.toLowerCase())) {
			loadCustomCategory(categories, categoryName.toLowerCase());
		}
	}

	public static void loadPredefinedCategory(HeadsCategories categories, String categoryName) throws MalformedURLException, SocketTimeoutException, IOException, UnsupportedOperationException
	{
		String loaderName = HeadsPlugin.getHeadsPlugin().getCategoriesConfig().getString("predefinedcategories." + categoryName + ".loader");
		DatabaseLoader loader;
		if (loaderName == null) {
			loader = HeadsPlugin.getDefaultDatabaseLoader();
		}
		else {
			switch (loaderName.toLowerCase()) {
				case "freshcoal":
				case "fc":
					loader = new FreshCoalLoader();
					break;
				case "mineskin":
				case "ms":
					loader = new MineSkinLoader();
					break;
				case "minecraftheads":
				case "minecraft-heads":
				case "mh":
					loader = new MinecraftHeadsLoader(null);
					break;
				default:
					loader = HeadsPlugin.getDefaultDatabaseLoader();
			}
		}
		String url = loader.getCategoriesUrl();
		loadPredefinedCategory(categories, url, categoryName, loader);
	}

	public static void loadCustomCategory(HeadsCategories categories, String categoryName) throws MalformedURLException, SocketTimeoutException, IOException, AuthenticationException, UnsupportedOperationException
	{
		String loaderName = HeadsPlugin.getHeadsPlugin().getCategoriesConfig().getString("customcategories." + categoryName + ".loader");
		DatabaseLoader loader;
		switch (loaderName.toLowerCase()) {
			case "freshcoal":
				loader = new FreshCoalLoader();
				break;
			case "mineskin":
				loader = new MineSkinLoader();
				break;
			case "minecraftheads":
			case "minecraft-heads":
				loader = new MinecraftHeadsLoader(null);
				break;
			default:
				loader = HeadsPlugin.getDefaultDatabaseLoader();
		}

		String url = loader.getSearchUrl();
		loadCustomCategory(categories, url, categoryName, loader);
	}

	public static void loadCategory(HeadsCategories categories, String predefinedCatagoryUrl, String customCatagoryUrl, String categoryName, DatabaseLoader loader) throws MalformedURLException, IOException, AuthenticationException
	{
		Set<String> predefinedCategoryNames = HeadsPlugin.getHeadsPlugin().getCategoriesConfig().getConfigurationSection("predefinedcategories").getKeys(false);
		Set<String> customCategoryNames = HeadsPlugin.getHeadsPlugin().getCategoriesConfig().getConfigurationSection("customcategories").getKeys(false);

		if (predefinedCategoryNames.contains(categoryName.toLowerCase())) {
			loadPredefinedCategory(categories, predefinedCatagoryUrl, categoryName.toLowerCase(), loader);
		}
		else if (customCategoryNames.contains(categoryName.toLowerCase())) {
			loadCustomCategory(categories, customCatagoryUrl, categoryName.toLowerCase(), loader);
		}
	}

	public static void loadPredefinedCategory(HeadsCategories categories, String url, String categoryName, DatabaseLoader loader) throws MalformedURLException, IOException
	{
		// try to get the id from the id object. If not present, probably the old notation is used, so get it straight from the name itself
		int id = HeadsPlugin.getHeadsPlugin().getCategoriesConfig().getInt("predefinedcategories." + categoryName + ".id", HeadsPlugin.getHeadsPlugin().getCategoriesConfig().getInt("predefinedcategories." + categoryName));

		HeadsPlugin.getHeadsPlugin().getLogger().info("Updating " + categoryName + ".json");
		String altCategoryName = HeadsPlugin.getHeadsPlugin().getCategoriesConfig().getString("predefinedcategories." + categoryName + ".categoryname", categoryName);
		try {
			HeadsCategory category = new HeadsCategory(categoryName, id);
			category.addAllHeads(loader.getHeads(url, URLEncoder.encode(altCategoryName, "UTF-8")));
			HeadsCacher.cacheCategory(category, HeadsPlugin.getHeadsPlugin().getDataFolder());
			categories.addCategory(category);
		}
		catch (SocketTimeoutException | UnknownHostException ex) {
			HeadsPlugin.getHeadsPlugin().getLogger().log(Level.INFO, null, ex);
			HeadsCategory category = new HeadsCategory(categoryName, id);
			category.addAllHeads(HeadsCacher.getHeads(categoryName, HeadsPlugin.getHeadsPlugin().getDataFolder()));
			categories.addCategory(category);
		}
	}

	public static void loadCustomCategory(HeadsCategories categories, String url, String categoryName, DatabaseLoader loader) throws MalformedURLException, IOException, AuthenticationException
	{
		int id = HeadsPlugin.getHeadsPlugin().getCategoriesConfig().getInt("customcategories." + categoryName + ".id");
		try {
			// <editor-fold defaultstate="collapsed">
            /*
            switch (HeadsPlugin.getHeadsPlugin().getAccessMode()) {
                case LITE:
                    throw new AuthenticationException("This feature is only available in the full version.");
                case EXPIRED:
                    throw new AuthenticationException("The trial has expired. This feature is only available in the full version.");
                case NONE:
                    throw new AuthenticationException("This server has not been registered yet or the key has not been added to the config file yet.");
            }
             */
			// </editor-fold>
			HeadsPlugin.getHeadsPlugin().getLogger().info("Updating " + categoryName + ".json");
			HeadsCategory category = new HeadsCategory(categoryName, id);
			List<String> headNames = HeadsPlugin.getHeadsPlugin().getCategoriesConfig().getConfigurationSection("customcategories").getConfigurationSection(categoryName).getStringList("urls");
			for (String headName : headNames) {
				category.addAllHeads(loader.getHeads(url, URLEncoder.encode(headName, "UTF-8")));
			}
			HeadsCacher.cacheCategory(category, HeadsPlugin.getHeadsPlugin().getDataFolder());
			categories.addCategory(category);
		}
		catch (SocketTimeoutException | UnknownHostException ex) {
			HeadsCategory category = new HeadsCategory(categoryName, id);
			category.addAllHeads(HeadsCacher.getHeads(categoryName, HeadsPlugin.getHeadsPlugin().getDataFolder()));
			categories.addCategory(category);
		}
	}

	//****** Getting a list of heads from a search result ******//
	public static List<Head> loadHeads(String headName, DatabaseLoader loader) throws MalformedURLException, SocketTimeoutException, IOException, AuthenticationException, UnsupportedOperationException
	{
		String url = loader.getSearchUrl();
		return loadHeads(url, headName, loader);
	}

	public static List<Head> loadHeads(String url, String headName, DatabaseLoader loader) throws MalformedURLException, SocketTimeoutException, IOException, AuthenticationException
	{
		// <editor-fold defaultstate="collapsed">
        /*
        switch (HeadsPlugin.getHeadsPlugin().getAccessMode()) {
            case LITE:
                throw new AuthenticationException("This feature is only available in the full version.");
            case EXPIRED:
                throw new AuthenticationException("The trial has expired. This feature is only available in the full version.");
            case NONE:
                throw new AuthenticationException("This server has not been registered yet or the key has not been added to the config file yet.");
        }
         */
		// </editor-fold>
		return loader.getHeads(url, URLEncoder.encode(headName, "UTF-8"));
	}

	//****** Getting the first head from a search result ******//
	public static Head loadHead(String headName, DatabaseLoader loader) throws MalformedURLException, SocketTimeoutException, IOException, AuthenticationException, UnsupportedOperationException
	{
		return loadHead(headName, 0, loader);
	}

	public static Head loadHead(String url, String headName, DatabaseLoader loader) throws MalformedURLException, SocketTimeoutException, IOException, AuthenticationException
	{
		return loadHead(url, headName, 0, loader);
	}

	//****** Getting the head at a certain index from a search result ******//
	public static Head loadHead(String headName, int index, DatabaseLoader loader) throws MalformedURLException, SocketTimeoutException, IOException, AuthenticationException, UnsupportedOperationException
	{
		String url = loader.getSearchUrl();
		return loadHead(url, headName, index, loader);
	}

	public static Head loadHead(String url, String headName, int index, DatabaseLoader loader) throws MalformedURLException, SocketTimeoutException, IOException, AuthenticationException
	{
		// <editor-fold defaultstate="collapsed">
        /*
        switch (HeadsPlugin.getHeadsPlugin().getAccessMode()) {
            case LITE:
                throw new AuthenticationException("This feature is only available in the full version.");
            case EXPIRED:
                throw new AuthenticationException("The trial has expired. This feature is only available in the full version.");
            case NONE:
                throw new AuthenticationException("This server has not been registered yet or the key has not been added to the config file yet.");
        }
         */
		// </editor-fold>
		List<Head> heads = loader.getHeads(url, URLEncoder.encode(headName, "UTF-8"));
		if (index >= heads.size()) {
			index = 0;
		}
		return heads.get(index);
	}
}
