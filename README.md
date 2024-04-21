# CloudNet v4 | JMX Support
This CloudNet module enables the possibility to track GC, memory and more via Java JMX.

## Focus
The purpose of these modules is to improve monitoring in the context of CloudNet in accordance with the JMX standard. It is not intended to create functionalities in the game.

## Motivation

As we have a very high standard and need for monitoring. And there is currently no easy way to create ports dynamically in JVM arguments, so this module has been dropped from the OneLiteFeather

## Usage
To use the function, the `jmx task <task> enabled <true/false>` command can be used to activate the function for each individual task.

## Developer
We also deliver an event with: `JMXExposeEvent`.
This can be used to register the server/proxy aka service in a third system
The API can be obtained with local publishing via `gradlew publishToMavenLocal`.

## Download
**We do not currently produce automatic downloads**
Please build it yourself, plus `gradlew jar`.