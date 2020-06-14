package com.github.cc007.headsplugin.config.bugfix;

import dev.alangomes.springspigot.command.CommandExecutor;
import dev.alangomes.springspigot.command.CommandResult;
import dev.alangomes.springspigot.configuration.DynamicValue;
import dev.alangomes.springspigot.configuration.Instance;
import dev.alangomes.springspigot.picocli.CommandLineDefinition;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import static org.apache.commons.lang3.BooleanUtils.toBoolean;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandException;
import org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Log4j2
@Primary
@Component
@ConditionalOnBean(annotation = CommandLine.Command.class)
public class FixedCommandExecutor implements CommandExecutor {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired(required = false)
    private AbstractAutoProxyCreator proxyCreator;

    @Autowired
    private CommandLineDefinition cli;

    @Setter(value = AccessLevel.PACKAGE)
    @DynamicValue("${spigot.messages.command_error:&cAn internal error occurred while attemping to perform this " +
            "command}")
    private Instance<String> commandErrorMessage;

    @Setter(value = AccessLevel.PACKAGE)
    @DynamicValue("${spigot.messages.missing_parameter_error:&cMissing parameter: %s}")
    private Instance<String> missingParameterErrorMessage;

    @Setter(value = AccessLevel.PACKAGE)
    @DynamicValue("${spigot.messages.parameter_error:&cInvalid parameter: %s}")
    private Instance<String> parameterErrorMessage;

    @Getter(value = AccessLevel.PACKAGE)
    @Setter(value = AccessLevel.PACKAGE)
    @DynamicValue("${spigot.commands.enable_cache:false}")
    private Instance<Boolean> cacheEnabled;

    private CommandLine commandLineCache;

    @Override
    public CommandResult execute(String... commandParts) {
        if (commandParts.length == 0) {
            return CommandResult.unknown();
        }
        try {
            if (!toBoolean(cacheEnabled.get()) || commandLineCache == null) {
                commandLineCache = cli.build(applicationContext);
            }
            val output = new ArrayList<String>();
            val commands = commandLineCache.parse(commandParts);

            if (commands.isEmpty()) {
                return CommandResult.unknown();
            }
            val commandLine = commands.get(commands.size() - 1);
            val command = proxyCreator != null ? proxyCreator.getEarlyBeanReference(commandLine.getCommand(), null) :
                    commandLine.getCommand();

            if (command instanceof Runnable) {
                ((Runnable) command).run();
            } else if (command instanceof Callable) {
                val result = ((Callable<?>) command).call();
                output.addAll(buildOutput(result));
            }
            return new CommandResult(output);
        } catch (CommandLine.InitializationException ex) {
            log.error("Unexpected exception during command initialization", ex);
            return CommandResult.unknown();
        } catch (CommandLine.UnmatchedArgumentException ex) {
            val commandObject = ex.getCommandLine()
                    .getCommandSpec()
                    .userObject();
            if (getBaseCommandClass().isInstance(commandObject)){
                return CommandResult.unknown();
            }
            val message = String.format(
                    parameterErrorMessage.get(),
                    String.join(", ", ex.getUnmatched())
            );
            return new CommandResult(ChatColor.translateAlternateColorCodes('&', message), true);
        } catch (CommandLine.MissingParameterException ex) {
            val message = String.format(
                    missingParameterErrorMessage.get(),
                    ex.getMissing()
                            .get(0)
                            .paramLabel()
            );
            return new CommandResult(ChatColor.translateAlternateColorCodes('&', message), true);
        } catch (CommandLine.ParameterException ex) {
            val message = String.format(
                    parameterErrorMessage.get(),
                    ex.getArgSpec()
                            .paramLabel()
            );
            return new CommandResult(ChatColor.translateAlternateColorCodes('&', message), true);
        } catch (CommandException ex) {
            return new CommandResult(ChatColor.RED + ex.getMessage(), true);
        } catch (Exception ex) {
            log.error("Unexpected exception while running /" + StringUtils.join(commandParts, " "), ex);
            return new CommandResult(ChatColor.translateAlternateColorCodes('&', commandErrorMessage.get()), true);
        }
    }

    @SneakyThrows
    private Class<?> getBaseCommandClass() {
        return Class.forName("dev.alangomes.springspigot.picocli.BaseCommand");
    }

    private List<String> buildOutput(Object result) {
        if (result instanceof String) {
            return Collections.singletonList(ChatColor.translateAlternateColorCodes('&', (String) result));
        } else if (result instanceof Collection) {
            return ((Collection<?>) result)
                    .stream()
                    .flatMap(res -> buildOutput(res).stream())
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

}
