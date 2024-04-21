package dev.onelitefeather.cloudnet.v4.jmxsupport.config;

public record JMXServiceTaskConfig(boolean enabled) {

    public static Builder builder() {
        return new Builder();
    }
    public static Builder builder(JMXServiceTaskConfig config) {
        return builder().enabled(config.enabled);
    }
    public static class Builder {

        private boolean enabled = false;

        public Builder enabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }
        public JMXServiceTaskConfig build() {
            return new JMXServiceTaskConfig(this.enabled);
        }
    }
}
