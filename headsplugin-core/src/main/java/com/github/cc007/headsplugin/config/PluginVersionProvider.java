package com.github.cc007.headsplugin.config;

import com.github.cc007.headsplugin.HeadsPlugin;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

@Component
public class PluginVersionProvider implements CommandLine.IVersionProvider
{

	@Override
	public String[] getVersion()
	{
		return new String[]{HeadsPlugin.context.getBean("pluginVersion", String.class)};
	}
}
