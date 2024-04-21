plugins {
    id("java")
    id("eu.cloudnetservice.juppiter") version "0.4.0"
    `maven-publish`
}

group = "dev.onelitefeather.cloudnet"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://jitpack.io/")
}

dependencies {
    // CloudNet
    compileOnly("eu.cloudnetservice.cloudnet:driver:4.0.0-RC10")
    compileOnly("eu.cloudnetservice.cloudnet:node:4.0.0-RC10")
    compileOnly("eu.cloudnetservice.cloudnet:bridge:4.0.0-RC10")
    // CloudNet Cloud Command Framework
    compileOnly("com.github.cloudnetservice.cloud-command-framework:cloud-core:1.9.0-cn1")
    compileOnly("com.github.cloudnetservice.cloud-command-framework:cloud-annotations:1.9.0-cn1")
}
java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}
tasks {
    compileJava {
        options.release.set(17)
        options.encoding = "UTF-8"
    }
}

moduleJson {
    name = "CloudNet-JMX-Support"
    author = "OneLiteFeather Org."
    main = "dev.onelitefeather.cloudnet.v4.jmxsupport.JMXSupportModule"
    description = "Node extension for the CloudNet runtime, which enables for Java Processes JMX Support"
}