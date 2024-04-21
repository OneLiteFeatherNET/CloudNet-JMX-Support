package dev.onelitefeather.cloudnet.v4.jmxsupport;

import dev.onelitefeather.cloudnet.v4.jmxsupport.commands.JMXCommand;
import dev.onelitefeather.cloudnet.v4.jmxsupport.config.JMXConfiguration;
import dev.onelitefeather.cloudnet.v4.jmxsupport.config.JMXServiceTaskConfig;
import dev.onelitefeather.cloudnet.v4.jmxsupport.listener.CloudNetLocalServiceListener;
import eu.cloudnetservice.common.language.I18n;
import eu.cloudnetservice.driver.document.DocumentFactory;
import eu.cloudnetservice.driver.event.EventManager;
import eu.cloudnetservice.driver.module.ModuleLifeCycle;
import eu.cloudnetservice.driver.module.ModuleTask;
import eu.cloudnetservice.driver.module.driver.DriverModule;
import eu.cloudnetservice.driver.provider.ServiceTaskProvider;
import eu.cloudnetservice.driver.service.ServiceTask;
import eu.cloudnetservice.node.command.CommandProvider;
import jakarta.inject.Singleton;

@Singleton
public final class JMXSupportModule extends DriverModule {

    private JMXConfiguration jmxConfiguration;

    @ModuleTask(order = 64)
    public void readConfig() {
        this.jmxConfiguration = this.readConfig(JMXConfiguration.class,
                () -> new JMXConfiguration(5000),
                DocumentFactory.json());
    }

    @ModuleTask(lifecycle = ModuleLifeCycle.STARTED, order = 64)
    public void addMissingJMXConfigurationEntries(ServiceTaskProvider taskProvider) {
        for (var task : taskProvider.serviceTasks()) {
            // check if the service task needs a smart entry
            if (!task.propertyHolder().contains("jmxConfig")) {
                var newTask = ServiceTask.builder(task)
                        .modifyProperties(properties -> properties.append("jmxConfig", JMXServiceTaskConfig.builder().build()))
                        .build();
                taskProvider.addServiceTask(newTask);
            }
        }
        I18n.loadFromLangPath(JMXSupportModule.class);
    }

    @ModuleTask(lifecycle = ModuleLifeCycle.STARTED)
    public void start(EventManager eventManager, CommandProvider commandProvider) {
        eventManager.registerListener(CloudNetLocalServiceListener.class);
        commandProvider.register(JMXCommand.class);
    }

    public JMXConfiguration config() {
        return this.jmxConfiguration;
    }
}
