package com.github.cc007.headsplugin.commands;

import com.github.cc007.headsplugin.commands.headsplugin.UpdateCategoryCommand;
import com.github.cc007.headsplugin.config.PluginVersionProvider;
import org.springframework.stereotype.Component;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Help.Ansi;

@Component
@Command(
	name = "headspluginapi",
	versionProvider = PluginVersionProvider.class,
	description = "Provides the commands for HeadsPlugin",
	aliases = {"headsplugin", "hpa", "hp"},
	mixinStandardHelpOptions = true,
	subcommands = {UpdateCategoryCommand.class}
)
public class HeadsPluginCommand extends AbstractCommand
{
	@Override
	public void run()
	{
		CommandLine cmd = commandSpec.commandLine();
		if(cmd.isUsageHelpRequested() || cmd.isVersionHelpRequested()) {
			context.getPlayer().sendMessage(chatManager.getChatBanner());
			if (cmd.isVersionHelpRequested()) {
				context.getPlayer().sendMessage(commandSpec.version());
			}
			if (cmd.isUsageHelpRequested()) {
				context.getPlayer().sendMessage(cmd.getUsageMessage(Ansi.OFF) + "\n ");
			}
		}
	}
}
