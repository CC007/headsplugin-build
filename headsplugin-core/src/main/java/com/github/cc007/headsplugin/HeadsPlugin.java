package com.github.cc007.headsplugin;

import com.github.cc007.headsplugin.config.Application;
import dev.alangomes.springspigot.SpringSpigotInitializer;
import org.bukkit.plugin.java.JavaPlugin;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

public class HeadsPlugin extends JavaPlugin
{

	public static ConfigurableApplicationContext context;

	@Override
	public void onDisable()
	{
		context.close();
		context = null;
	}

	@Override
	public void onEnable()
	{
		saveDefaultConfig();
		ResourceLoader loader = new DefaultResourceLoader(getClassLoader());
		SpringApplication application = new SpringApplication(loader, Application.class);
		application.addInitializers(new SpringSpigotInitializer(this));
		context = application.run();
	}

}
