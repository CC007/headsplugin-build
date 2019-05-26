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
package com.github.cc007.headsplugin.legacy;

import com.github.cc007.headsplugin.legacy.listeners.HeadsCommandListener;
import com.github.cc007.headsplugin.legacy.utils.HeadsUtils;
import com.github.cc007.headsplugin.legacy.utils.authentication.AccessMode;
import com.github.cc007.headsplugin.legacy.utils.authentication.KeyAuthenticator;
import com.github.cc007.headsplugin.legacy.utils.heads.HeadsCategory;
import com.github.cc007.headsplugin.legacy.utils.loader.DatabaseLoader;
import com.github.cc007.headsplugin.legacy.utils.loader.FreshCoalLoader;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Rik Schaaf aka CC007 (http://coolcat007.nl/)
 */
public class HeadsPlugin extends JavaPlugin
{

	private Logger log;
	private HeadsUtils headsUtils;
	private FileConfiguration categoriesConfig = null;
	private File categoriesConfigFile = null;
	private FileConfiguration config = null;
	private File configFile = null;
	private AccessMode accessMode;

	public static HeadsPlugin getHeadsPlugin()
	{
		Plugin headsPlugin = Bukkit.getServer().getPluginManager().getPlugin("HeadsPluginAPI");
		if (headsPlugin != null && headsPlugin.isEnabled() && headsPlugin instanceof HeadsPlugin) {
			return (HeadsPlugin) headsPlugin;
		}
		else {
			Bukkit.getLogger().log(Level.WARNING, "The heads plugin has not been enabled yet");
			return null;
		}
	}

	/**
	 * get the minecraft chat prefix for this plugin
	 *
	 * @param colored whether or not the prefix should be colored
	 * @return the minecraft chat prefix for this plugin
	 */
	public static String pluginChatPrefix(boolean colored)
	{
		if (colored) {
			return ChatColor.DARK_AQUA + "[" + ChatColor.GOLD + "Heads" + ChatColor.GREEN + "Plugin" + ChatColor.AQUA + "API" + ChatColor.DARK_AQUA + "]" + ChatColor.WHITE + " ";
		}
		else {
			return "[HeadsPluginAPI] ";
		}
	}

	public static DatabaseLoader getDefaultDatabaseLoader()
	{
		return new FreshCoalLoader();//TODO configurable;
	}

	@Override
	public void onEnable()
	{
		/* Setup the logger */
		log = getLogger();

		/* Config stuffs */
		getCategoriesConfig().options().copyDefaults(true);
		getConfig().options().copyDefaults(true);
		saveDefaultConfig();

		/* 1.9.0 specific code to clean up cache files (they were moved to the cache folder) */
		File dir = getDataFolder();
		FileFilter fileFilter = new WildcardFileFilter("*.json");
		File[] fileList = dir.listFiles(fileFilter);
		if (fileList.length > 0) {
			log.warning("cache files were found outside of the cache folder. In version HeadsPluginAPI 1.9.0 and later you can find the heads cache in the folder <serverLocation>/plugins/HeadsPluginAPI/cache");
		}
		for (File file : fileList) {
			file.delete();
		}

		try {
			addRootCA();
		}
		catch (CertificateException ex) {
			log.warning("Something went wrong with adding a certificate for freshcoal. This can happen when using the /reload command. In that case the exception can be ignored: " + ex.getMessage());
		}
		catch (IOException | KeyManagementException | KeyStoreException | NoSuchAlgorithmException ex) {
			log.log(Level.SEVERE, null, ex);
		}

		//authenticate();

		/* Register the headsplugin command */
		getCommand("headsplugin").setExecutor(new HeadsPluginCommand(this));

		/* Register the /heads command listener */
		getServer().getPluginManager().registerEvents(new HeadsCommandListener(), this);

		/* Setup the utils */
		headsUtils = HeadsUtils.getInstance(log);

		/* Load the categories */
		Thread t = new Thread()
		{

			@Override
			public void run()
			{
				try {
					headsUtils.loadCategories();
				}
				catch (SocketTimeoutException ex) {
					try {
						headsUtils.loadCategories();
					}
					catch (SocketTimeoutException ex2) {
						log.severe("The server did not respond. Please check if the heads website is online.");
						log.log(Level.SEVERE, null, ex2);
					}
					catch (MalformedURLException ex2) {
						log.severe("The url is malformed. Please check the config file");
						log.log(Level.SEVERE, null, ex2);
					}
					catch (IOException ex2) {
						log.severe("An unknown exception has occurred. Please check if the heads website is online.");
						log.log(Level.SEVERE, null, ex2);
					}
				}
				catch (MalformedURLException ex) {
					log.severe("The url is malformed. Please check the config file");
					log.log(Level.SEVERE, null, ex);
				}
				catch (IOException ex) {
					log.severe("An unknown exception has occurred. Please check if the heads website is online.");
					log.log(Level.SEVERE, null, ex);
				}
			}

		};

		t.start();
	}

	@Override
	public void onDisable()
	{
		if (headsUtils != null) {
			for (HeadsCategory category : headsUtils.getCategories().getList()) {
				category.clear();
			}
			headsUtils.getCategories().clear();
			headsUtils = null;
		}
	}

	/**
	 * Gets a plugin
	 *
	 * @param pluginName Name of the plugin to get
	 * @return The plugin from name
	 */
	public Plugin getPlugin(String pluginName)
	{
		if (getServer().getPluginManager().getPlugin(pluginName) != null && getServer().getPluginManager().getPlugin(pluginName).isEnabled()) {
			return getServer().getPluginManager().getPlugin(pluginName);
		}
		else {
			getLogger().log(Level.WARNING, "Could not find plugin \"{0}\"!", pluginName);
			return null;
		}
	}

	public void authenticate()
	{
		/* Check if the config is correctly configured */
		if (getConfig().getString("authenticationkey").equals("0")) {
			log.severe("The HeadsPlugin config file has not been correctly configured yet. Make sure that the authentication key and world are set.");
		}

		/* Set the access mode of the plugin */
		this.accessMode = KeyAuthenticator.getAccessMode(getConfig().getString("authenticationkey"));
	}

	/**
	 * Method to reload the config.yml config file
	 */
	@Override
	public void reloadConfig()
	{
		if (configFile == null) {
			configFile = new File(getDataFolder(), "config.yml");
		}
		config = YamlConfiguration.loadConfiguration(configFile);

		// Look for defaults in the jar
		Reader defConfigStream = null;
		try {
			defConfigStream = new InputStreamReader(this.getResource("config.yml"), "UTF8");
		}
		catch (UnsupportedEncodingException ex) {
			getLogger().log(Level.SEVERE, null, ex);
		}
		if (defConfigStream != null) {
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			config.setDefaults(defConfig);
		}
	}

	/**
	 * Method to get YML content of the config.yml config file
	 *
	 * @return YML content of the catagories.yml config file
	 */
	@Override
	public FileConfiguration getConfig()
	{
		if (config == null) {
			reloadConfig();
		}
		return config;
	}

	/**
	 * Method to save the config.yml config file
	 */
	@Override
	public void saveConfig()
	{
		if (config == null || configFile == null) {
			return;
		}
		try {
			getConfig().save(configFile);
		}
		catch (IOException ex) {
			getLogger().log(Level.SEVERE, "Could not save config to " + configFile, ex);
		}
	}

	/**
	 * Method to reload the categories.yml config file
	 */
	public void reloadCategoriesConfig()
	{
		if (categoriesConfigFile == null) {
			categoriesConfigFile = new File(getDataFolder(), "categories.yml");
		}
		categoriesConfig = YamlConfiguration.loadConfiguration(categoriesConfigFile);

		// Look for defaults in the jar
		Reader defConfigStream = null;
		try {
			defConfigStream = new InputStreamReader(this.getResource("categories.yml"), "UTF8");
		}
		catch (UnsupportedEncodingException ex) {
			getLogger().log(Level.SEVERE, null, ex);
		}
		if (defConfigStream != null) {
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			categoriesConfig.setDefaults(defConfig);
		}
	}

	/**
	 * Method to get YML content of the categories.yml config file
	 *
	 * @return YML content of the categories.yml config file
	 */
	public FileConfiguration getCategoriesConfig()
	{
		if (categoriesConfig == null) {
			reloadCategoriesConfig();
		}
		return categoriesConfig;
	}

	/**
	 * Method to save the catagories.yml config file
	 */
	public void saveCategoriesConfig()
	{
		if (categoriesConfig == null || categoriesConfigFile == null) {
			return;
		}
		try {
			getCategoriesConfig().save(categoriesConfigFile);
		}
		catch (IOException ex) {
			getLogger().log(Level.SEVERE, "Could not save config to " + categoriesConfigFile, ex);
		}
	}

	/**
	 * Method to save the default config file
	 */
	@Override
	public void saveDefaultConfig()
	{
		String version = getConfig().getString("version", null);
		if (categoriesConfigFile == null) {
			categoriesConfigFile = new File(getDataFolder(), "categories.yml");
		}
		if (!categoriesConfigFile.exists()) {
			saveResource("categories.yml", false);
			reloadCategoriesConfig();
		}
		else if (!getDescription().getVersion().equals(version)) {
			getLogger().log(
				Level.WARNING, "New version detected: {0}->{1}. Saving new default categories.",
				new Object[]{(version == null ? "(?)" : version), getDescription().getVersion()}
			);
			saveResource("categories.yml", true);
			reloadCategoriesConfig();
		}
		if (configFile == null) {
			configFile = new File(getDataFolder(), "config.yml");
		}
		if (!configFile.exists()) {
			saveResource("config.yml", false);
			reloadConfig();
		}
		else if (!getDescription().getVersion().equals(version)) {
			getLogger().log(
				Level.WARNING, "New version detected: {0}->{1}. Saving new default config.",
				new Object[]{(version == null ? "(?)" : version), getDescription().getVersion()}
			);
			saveResource("config.yml", true);
			reloadConfig();
		}
	}

	/**
	 * Get the HeadUtils instance
	 *
	 * @return the HeadUtils instance
	 */
	public HeadsUtils getHeadsUtils()
	{
		return headsUtils;
	}

	public AccessMode getAccessMode()
	{
		return accessMode;
	}

	public void addRootCA() throws CertificateException, IOException, KeyManagementException, KeyStoreException, NoSuchAlgorithmException
	{
		try (InputStream fis = new BufferedInputStream(this.getClassLoader().getResourceAsStream("letsencrypt.crt"))) {
			Certificate ca = CertificateFactory.getInstance("X.509").generateCertificate(fis);
			KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
			Path ksPath = Paths.get(System.getProperty("java.home"), "lib", "security", "cacerts");
			ks.load(Files.newInputStream(ksPath), "changeit".toCharArray());
			ks.setCertificateEntry("LetsEncrypt CA", ca);
			TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			tmf.init(ks);
			SSLContext ctx = SSLContext.getInstance("TLS");
			ctx.init(null, tmf.getTrustManagers(), null);
			HttpsURLConnection.setDefaultSSLSocketFactory(ctx.getSocketFactory());
		}
	}

}
