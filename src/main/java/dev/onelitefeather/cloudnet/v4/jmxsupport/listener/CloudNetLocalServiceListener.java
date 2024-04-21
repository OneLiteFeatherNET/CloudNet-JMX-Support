package dev.onelitefeather.cloudnet.v4.jmxsupport.listener;

import dev.onelitefeather.cloudnet.v4.jmxsupport.JMXSupportModule;
import dev.onelitefeather.cloudnet.v4.jmxsupport.config.JMXServiceTaskConfig;
import dev.onelitefeather.cloudnet.v4.jmxsupport.events.JMXExposeEvent;
import eu.cloudnetservice.driver.event.EventListener;
import eu.cloudnetservice.driver.event.EventManager;
import eu.cloudnetservice.driver.network.HostAndPort;
import eu.cloudnetservice.driver.service.ServiceConfiguration;
import eu.cloudnetservice.node.config.Configuration;
import eu.cloudnetservice.node.event.service.CloudServiceConfigurationPrePrepareEvent;
import eu.cloudnetservice.node.util.NetworkUtil;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.ArrayList;

@Singleton
public final class CloudNetLocalServiceListener {

    private final Configuration configuration;
    private final JMXSupportModule jmxSupportModule;
    private final EventManager eventManager;
    private static final String JVM_RMI_HOSTNAME = "-Djava.rmi.server.hostname=%s";
    private static final String JVM_JMX_REMOTE = "-Dcom.sun.management.jmxremote=true";
    private static final String JVM_JMX_REMOTE_PORT = "-Dcom.sun.management.jmxremote.port=%d";
    private static final String JVM_JMX_REMOTE_AUTH = "-Dcom.sun.management.jmxremote.authenticate=false";
    private static final String JVM_JMX_REMOTE_SSL = "-Dcom.sun.management.jmxremote.ssl=false";

    @Inject
    public CloudNetLocalServiceListener(Configuration configuration, JMXSupportModule jmxSupportModule, EventManager eventManager) {
        this.configuration = configuration;
        this.jmxSupportModule = jmxSupportModule;
        this.eventManager = eventManager;
    }

    @EventListener
    public void handlePrePrepare(CloudServiceConfigurationPrePrepareEvent event) {
        var property = event.originalConfiguration().propertyHolder().readObject("jmxConfig", JMXServiceTaskConfig.class);
        if (property != null && property.enabled()) {
            var newJvmOption = new ArrayList<>(event.originalConfiguration().jvmOptions());
            var jmxPort = findFreeServicePort(event.originalConfiguration(), this.configuration.hostAddress());
            newJvmOption.add(JVM_RMI_HOSTNAME.formatted(this.configuration.hostAddress()));
            newJvmOption.add(JVM_JMX_REMOTE);
            newJvmOption.add(JVM_JMX_REMOTE_PORT.formatted(jmxPort));
            newJvmOption.add(JVM_JMX_REMOTE_SSL);
            newJvmOption.add(JVM_JMX_REMOTE_AUTH);
            ServiceConfiguration.Builder builder = event.modifiableConfiguration().jvmOptions(newJvmOption);
            this.eventManager.callEvent(new JMXExposeEvent(new HostAndPort(this.configuration.hostAddress(), jmxPort), builder.build()));
        }
    }

    private int findFreeServicePort(
            ServiceConfiguration configuration,
            String hostAddress
    ) {
        // increase the port number until we found a port
        var port = this.jmxSupportModule.config().startPort();
        while (NetworkUtil.isInUse(hostAddress, port)) {
            port++;

            // stop if the port exceeds the possible port range
            if (port > 0xFFFF) {
                throw new IllegalStateException("No free port found for service, started at port: " + configuration.port());
            }
        }

        // use the next free, available port
        return port;
    }

}
