package dev.onelitefeather.cloudnet.v4.jmxsupport.events;

import eu.cloudnetservice.driver.event.Event;
import eu.cloudnetservice.driver.network.HostAndPort;
import eu.cloudnetservice.driver.service.ServiceConfiguration;

public final class JMXExposeEvent extends Event {

    private final HostAndPort jmxAccess;
    private final ServiceConfiguration serviceConfiguration;

    public JMXExposeEvent(HostAndPort jmxAccess, ServiceConfiguration serviceConfiguration) {
        this.jmxAccess = jmxAccess;
        this.serviceConfiguration = serviceConfiguration;
    }

    public HostAndPort getJmxAccess() {
        return jmxAccess;
    }

    public ServiceConfiguration getServiceConfiguration() {
        return serviceConfiguration;
    }
}
