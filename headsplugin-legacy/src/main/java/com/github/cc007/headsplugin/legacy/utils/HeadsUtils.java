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
package com.github.cc007.headsplugin.legacy.utils;

import com.github.cc007.headsplugin.legacy.HeadsPlugin;
import com.github.cc007.headsplugin.legacy.exceptions.AuthenticationException;
import com.github.cc007.headsplugin.legacy.utils.heads.Head;
import com.github.cc007.headsplugin.legacy.utils.heads.HeadsCategories;
import com.github.cc007.headsplugin.legacy.utils.heads.HeadsCategory;
import com.github.cc007.headsplugin.legacy.utils.loader.DatabaseLoader;
import com.github.cc007.headsplugin.legacy.utils.loader.HeadsLoader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.entity.Player;

/**
 * @author Rik Schaaf aka CC007 (http://coolcat007.nl/)
 */
public class HeadsUtils
{

	private static Logger log;
	private HeadsCategories categories;
	private DatabaseLoader loader = HeadsPlugin.getDefaultDatabaseLoader();

	/**
	 * private constructor of the HeadsUtils class
	 *
	 * @param log the logger that this class and its dependent classes should
	 *            use
	 */
	private HeadsUtils(Logger log)
	{
		if (log == null) {
			HeadsUtils.log = HeadsPlugin.getHeadsPlugin().getLogger();
			HeadsUtils.log.log(Level.WARNING, "No logger provided, using default logger");
		}
		else {
			HeadsUtils.log = log;
		}
		this.categories = new HeadsCategories();
	}

	/**
	 * Get the instance of this singleton class (or create a new one if none
	 * exist).
	 *
	 * @param log             the logger that this class and its dependent classes should
	 *                        use
	 * @param overwriteLogger if true and a logger was previously set, it will
	 *                        be overridden
	 * @return the instance of this singleton class
	 */
	public static HeadsUtils getInstance(Logger log, boolean overwriteLogger)
	{
		if (NewheadsUtilsHolder.instance == null) {
			NewheadsUtilsHolder.instance = new HeadsUtils(log);
		}
		else if (log != null && (!log.equals(HeadsUtils.getLogger()))) {
			if (!overwriteLogger) {
				log.log(Level.WARNING, "HeadsUtils is already registered to use a different logger, ignoring new logger");
			}
			else {
				HeadsUtils.log = log;
			}
		}
		return NewheadsUtilsHolder.instance;
	}

	/**
	 * Get the instance of this singleton class (or create a new one if none
	 * exist).
	 *
	 * @param log the logger that this class and its dependent classes should
	 *            use
	 * @return the instance of this singleton class
	 */
	public static HeadsUtils getInstance(Logger log)
	{
		return getInstance(log, false);
	}

	/**
	 * Get the instance of this singleton class (or create a new one if none
	 * exist).
	 * <p>
	 * If the class was never used before, it will log a warning that the
	 * default logger will be used.
	 *
	 * @return the instance of this singleton class
	 */
	public static HeadsUtils getInstance()
	{
		return getInstance(null, false);
	}

	/**
	 * Get the logger that is used by HeadsUtils and its dependent classes
	 *
	 * @return the logger
	 */
	public static Logger getLogger()
	{
		return log;
	}

	/**
	 * Get the value of categories
	 *
	 * @return the value of categories
	 */
	public HeadsCategories getCategories()
	{
		return categories;
	}

	/**
	 * Set the value of categories
	 *
	 * @param categories new value of categories
	 */
	public void setCategories(HeadsCategories categories)
	{
		this.categories = categories;
	}

	/**
	 * Load the categories from the database. It uses the url links to the heads
	 * server, specified in the config.yml file
	 *
	 * @throws java.net.MalformedURLException if the url from the config file
	 *                                        was malformed
	 * @throws java.io.IOException            if loading caused any IO error
	 * @throws UnsupportedOperationException  if the database doesn't support doing loading some of the categories
	 *                                        (why depends on whether it is a predefined or a custom category)
	 */
	public void loadCategories() throws MalformedURLException, IOException, UnsupportedOperationException
	{
		try {
			HeadsLoader.loadCategories(categories);
		}
		catch (AuthenticationException ex) {
			log.log(Level.WARNING, "{0} Only the predefined categories were loaded", ex.getMessage());
		}
	}

	/**
	 * Load the categories from the database
	 *
	 * @param predefinedUrl link to the predefined categories API
	 * @param customUrl     link to the uncategorized API
	 * @throws java.net.MalformedURLException if the supplied urls were
	 *                                        malformed
	 * @throws java.io.IOException            if loading caused any IO error
	 * @throws UnsupportedOperationException  if the database doesn't support doing loading some of the categories
	 *                                        (why depends on whether it is a predefined or a custom category)
	 */
	public void loadCategories(String predefinedUrl, String customUrl) throws MalformedURLException, IOException
	{
		HeadsLoader.loadPredefinedCategories(categories, predefinedUrl, loader);
		try {
			HeadsLoader.loadCustomCategories(categories, customUrl, loader);
		}
		catch (AuthenticationException ex) {
			log.log(Level.WARNING, "{0} Only the predefined categories were loaded", ex.getMessage());
		}
	}

	/**
	 * Load one category from the database. It uses the url links to the heads
	 * server, specified in the config.yml file
	 *
	 * @param categoryName the name of the category
	 * @throws java.net.MalformedURLException                                         if the url from the config file
	 *                                                                                was malformed
	 * @throws java.io.IOException                                                    if loading caused any IO error
	 * @throws com.github.cc007.headsplugin.legacy.exceptions.AuthenticationException if a method is used that is not allowed in the current access mode
	 * @throws UnsupportedOperationException                                          if the database doesn't support doing loading that category
	 *                                                                                (why depends on whether it is a predefined or a custom category)
	 */
	public void loadCategory(String categoryName) throws MalformedURLException, IOException, AuthenticationException, UnsupportedOperationException
	{
		HeadsLoader.loadCategory(categories, categoryName);
	}

	/**
	 * Load one category from the database
	 *
	 * @param predefinedUrl link to the predefined categories API
	 * @param customUrl     link to the uncategorized API
	 * @param categoryName  the name of the category
	 * @throws java.net.MalformedURLException                                         if the supplied urls were
	 *                                                                                malformed
	 * @throws java.io.IOException                                                    if loading caused any IO error
	 * @throws com.github.cc007.headsplugin.legacy.exceptions.AuthenticationException if a method is used that is not allowed in the current access mode
	 * @throws UnsupportedOperationException                                          if the database doesn't support doing loading that category
	 *                                                                                (why depends on whether it is a predefined or a custom category)
	 */
	public void loadCategory(String predefinedUrl, String customUrl, String categoryName) throws MalformedURLException, IOException, AuthenticationException, UnsupportedOperationException
	{
		HeadsLoader.loadCategory(categories, predefinedUrl, customUrl, categoryName, loader);
	}

	/**
	 * Get all heads from all categories
	 *
	 * @return all heads from all categories
	 */
	public List<Head> getAllCategoryHeads()
	{
		List<Head> hList = new ArrayList<>();
		Set<String> categoryNames = HeadsPlugin.getHeadsPlugin().getCategoriesConfig().getConfigurationSection("predefinedcategories").getKeys(false);
		for (String categoryName : categoryNames) {
			HeadsCategory category = categories.getCategory(categoryName);
			if (category != null) {
				hList.addAll(category.getList());
			}
		}
		Set<String> customCategoryNames = HeadsPlugin.getHeadsPlugin().getCategoriesConfig().getConfigurationSection("customcategories").getKeys(false);
		for (String categoryName : customCategoryNames) {
			HeadsCategory category = categories.getCategory(categoryName);
			if (category != null) {
				hList.addAll(category.getList());
			}
		}
		return hList;
	}

	/**
	 * Get all heads from a certain category by name
	 *
	 * @param categoryName the name of the category
	 * @return all heads from a certain category
	 */
	public List<Head> getCategoryHeads(String categoryName)
	{
		HeadsCategory category = categories.getCategory(categoryName);
		return category == null ? null : category.getList();
	}

	/**
	 * Get all heads from a certain category by id
	 *
	 * @param id the id of the category
	 * @return all heads from a certain category
	 */
	public List<Head> getCategoryHeads(int id)
	{
		HeadsCategory category = categories.getCategory(id);
		return category == null ? null : category.getList();
	}

	/**
	 * Get all heads with a certain name
	 *
	 * @param headName the name of the heads
	 * @return all heads with a certain name
	 * @throws java.net.MalformedURLException                                         if the url from the config file
	 *                                                                                was malformed
	 * @throws java.net.SocketTimeoutException                                        if the website to which the url
	 *                                                                                links doesn't respond
	 * @throws java.io.IOException                                                    if loading caused any IO error
	 * @throws com.github.cc007.headsplugin.legacy.exceptions.AuthenticationException if a method is used that is not allowed in the current access mode
	 * @throws UnsupportedOperationException                                          if the database doesn't support doing custom head searches
	 */
	public List<Head> getHeads(String headName) throws MalformedURLException, SocketTimeoutException, IOException, AuthenticationException, UnsupportedOperationException
	{
		return HeadsLoader.loadHeads(headName, loader);
	}

	/**
	 * Get a specific head (first available head if there are more)
	 *
	 * @param headName the name of the head
	 * @return the head
	 * @throws java.net.MalformedURLException                                         if the url from the config file
	 *                                                                                was malformed
	 * @throws java.net.SocketTimeoutException                                        if the website to which the url
	 *                                                                                links doesn't respond
	 * @throws java.io.IOException                                                    if loading caused any IO error
	 * @throws com.github.cc007.headsplugin.legacy.exceptions.AuthenticationException if a method is used that is not allowed in the current access mode
	 * @throws UnsupportedOperationException                                          if the database doesn't support doing custom head searches
	 */
	public Head getHead(String headName) throws MalformedURLException, SocketTimeoutException, IOException, AuthenticationException, UnsupportedOperationException
	{
		return HeadsLoader.loadHead(headName, loader);
	}

	/**
	 * Get a specific head (first available head if the index is higher than the
	 * number of heads with this head name)
	 *
	 * @param headName the name of the head
	 * @param index    number in the list of heads
	 * @return the head
	 * @throws java.net.MalformedURLException                                         if the url from the config file
	 *                                                                                was malformed
	 * @throws java.net.SocketTimeoutException                                        if the website to which the url
	 *                                                                                links doesn't respond
	 * @throws java.io.IOException                                                    if loading caused any IO error
	 * @throws com.github.cc007.headsplugin.legacy.exceptions.AuthenticationException if a method is used that is not allowed in the current access mode
	 * @throws UnsupportedOperationException                                          if the database doesn't support doing custom head searches
	 */
	public Head getHead(String headName, int index) throws MalformedURLException, SocketTimeoutException, IOException, AuthenticationException, UnsupportedOperationException
	{
		return HeadsLoader.loadHead(headName, index, loader);
	}

	/**
	 * Add the head of the given player to the database under the given name
	 *
	 * @param player   the player who's head will be added to the database
	 * @param headName the name of the head that will be added to the database
	 * @return the head that was generated or null if the head could not be added to the database
	 * @throws java.net.MalformedURLException                                         if the url from the config file
	 *                                                                                was malformed
	 * @throws java.net.SocketTimeoutException                                        if the website to which the url
	 *                                                                                links doesn't respond
	 * @throws java.io.IOException                                                    if loading caused any IO error
	 * @throws com.github.cc007.headsplugin.legacy.exceptions.AuthenticationException if a method is used that is not allowed in the current access mode
	 * @throws UnsupportedOperationException                                          if the database doesn't support saving a head
	 */
	public Head saveHead(Player player, String headName) throws MalformedURLException, SocketTimeoutException, IOException, AuthenticationException, UnsupportedOperationException
	{
		return loader.addHead(loader.getGenerateUrl(), player.getUniqueId(), headName);
	}

	/**
	 * Set which database loader to use to load the heads from a database
	 *
	 * @param loader the loader to load the heads from a database
	 */
	public void setDatabaseLoader(DatabaseLoader loader)
	{
		this.loader = loader;
	}

	private static class NewheadsUtilsHolder
	{

		private static HeadsUtils instance;
	}

}
