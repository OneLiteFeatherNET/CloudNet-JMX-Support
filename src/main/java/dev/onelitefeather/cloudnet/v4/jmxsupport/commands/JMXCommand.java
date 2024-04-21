package dev.onelitefeather.cloudnet.v4.jmxsupport.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.parsers.Parser;
import cloud.commandframework.annotations.suggestions.Suggestions;
import cloud.commandframework.context.CommandContext;
import dev.onelitefeather.cloudnet.v4.jmxsupport.config.JMXServiceTaskConfig;
import eu.cloudnetservice.common.language.I18n;
import eu.cloudnetservice.driver.provider.ServiceTaskProvider;
import eu.cloudnetservice.driver.service.ServiceTask;
import eu.cloudnetservice.node.command.annotation.Description;
import eu.cloudnetservice.node.command.exception.ArgumentNotAvailableException;
import eu.cloudnetservice.node.command.source.CommandSource;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.List;
import java.util.Queue;
import java.util.function.Function;

@Singleton
@CommandPermission("cloudnet.command.jmx")
@Description("module-jmx-command-description")
public final class JMXCommand {

    private final ServiceTaskProvider taskProvider;

    @Inject
    public JMXCommand(ServiceTaskProvider taskProvider) {
        this.taskProvider = taskProvider;
    }

    @Suggestions("jmxTask")
    public List<String> suggestSmartTasks(CommandContext<?> $, String input) {
        return this.taskProvider.serviceTasks()
                .stream()
                .filter(serviceTask -> serviceTask.propertyHolder().contains("jmxConfig"))
                .map(ServiceTask::name)
                .toList();
    }

    @Parser(name = "jmxTask", suggestions = "jmxTask")
    public ServiceTask smartTaskParser( CommandContext<?> $,  Queue<String> input) {
        var task = this.taskProvider.serviceTask(input.remove());
        if (task == null) {
            throw new ArgumentNotAvailableException(I18n.trans("command-tasks-task-not-found"));
        }
        // only allow tasks with the smart config
        if (!task.propertyHolder().contains("jmxConfig")) {
            throw new ArgumentNotAvailableException(I18n.trans("module-jmx-command-task-no-entry", task.name()));
        }
        return task;
    }

    @CommandMethod("jmx task <task> enabled <enabled>")
    public void enable(
            CommandSource source,
            @Argument(value = "task", parserName = "jmxTask") ServiceTask task,
            @Argument("enabled") boolean enabled
    ) {
        this.updateJMX(task, config -> config.enabled(enabled));
        source.sendMessage(I18n.trans(
                "command-tasks-set-property-success",
                "enabled", task.name(),
                enabled));
    }

    private void updateJMX(
            ServiceTask serviceTask,
            Function<JMXServiceTaskConfig.Builder, JMXServiceTaskConfig.Builder> modifier
    ) {
        // read the smart config from the task
        var property = serviceTask.propertyHolder().readObject("jmxConfig", JMXServiceTaskConfig.class);

        // rewrite the config and update it in the cluster
        var task = ServiceTask.builder(serviceTask)
                .modifyProperties(properties -> {
                    var newSmartConfigEntry = modifier.apply(JMXServiceTaskConfig.builder(property)).build();
                    properties.append("jmxConfig", newSmartConfigEntry);
                })
                .build();
        this.taskProvider.addServiceTask(task);
    }


}
