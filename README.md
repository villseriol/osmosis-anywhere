# Osmosis Example

This is a template repository that shows how to scaffold a maven project that creates an osmosis
plugin.

## Installation

The following command will produce a `.zip` file which is the osmosis plugin.

```sh
mvn clean package
```

## Enabling Detailed Logs

```sh
# create backup of old properties
sudo mv /etc/osmosis/log4j.properties /etc/osmosis/log4j.properties.bak

# replace with debug properties
sudo cp src/test/resources/log4j.properties /etc/osmosis/log4j.properties
```

```sh
# these variables will be picked up by the osmosis tool
export JAVACMD_OPTIONS="-Dsun.misc.URLClassPath.debug=true -Dsun.misc.URLClassPath.debugLookupCache=true -Djdk.net.URLClassPath.disableClassPathURLCheck=debug -Dsun.cds.enableSharedLookupCache=true"
export OSMOSIS_OPTIONS="-v"
```

## JPF 1.5 Limitations

The Java Plugin Framework (JPF) is used by osmosis to load plugins at runtime. If you are
integrating with the debian osmosis command line utility, JPF loading is the only viable integration
method. By default, osmosis will look at the `plugins` folder in the current working directory or
the `~/.openstreetmap/osmosis/plugins` directory.

I encountered a lot of trouble with plugin loading and eventually settled on unpacking the artifact
`.jar` directly into the plugin `.zip`. I have documented the troubleshooting steps that I took to
fix `.jar` loading.

### Troubleshooting

I believe the root cause of `.jar` loading issues is the `.zip` into `.jar` nesting issues. This
shading path resolver will copy and extract the zip plugin to a temporary directory instead of
loading directly from the `.zip`.

The indication that this is the root cause is:

1. Plugins load fine if you just extract the `.jar` file manually
2. The log line
   `jar:file:/home/ey4180/dev/osmosis-plexus/target/plugins/osmosis-plexus-1.0.0-plugin.zip!/osmosis-plexus-1.0.0.jar`

#### JPF Configuration

Another way of fixing this could be to specify the `jpf.properties` file in the classpath and use
the `org.java.plugin.standard.ShadingPathResolver` instead of the standard one.

Look in the `.bashrc` file for the java options to output debug information. I also had to edit
`/etc/osmosis/log4j.properties` and set the log level to `DEBUG`.

```properties
# Java Plug-in Framework (JPF)
# Copyright (C) 2004 - 2005 Dmitry Olshansky
# $Id$

# Default JPF configuration. All properties are optional.

# Object factory implementation
org.java.plugin.ObjectFactory = org.java.plugin.standard.StandardObjectFactory

# Plug-in registry implementation
org.java.plugin.registry.PluginRegistry = org.java.plugin.registry.xml.PluginRegistryImpl
org.java.plugin.registry.xml.PluginRegistryImpl.isValidating = true
org.java.plugin.registry.xml.PluginRegistryImpl.stopOnError = false

# Standard (simple) path resolver implementation
org.java.plugin.PathResolver = org.java.plugin.standard.StandardPathResolver
# Files shading (anti-locking) path resolver implementation
#org.java.plugin.PathResolver = org.java.plugin.standard.ShadingPathResolver
#org.java.plugin.standard.ShadingPathResolver.shadowFolder = ./temp/.jpf-shadow
#org.java.plugin.standard.ShadingPathResolver.unpackMode = smart

# Plug-in life cycle handler implementation
org.java.plugin.standard.PluginLifecycleHandler = org.java.plugin.standard.StandardPluginLifecycleHandler
```

> Because osmosis uses the plexus-classworlds launcher the classpath is locked down. The only way to
> solve this is to add the properties in the `${java.home}/lib` folder. I suppose we could try and
> convince the upsteam maintainers to update the plexus config to allow user-defined properties.

#### Multi-POM Project

I also investigated if a multi-module project would bypass the issues. I thought that we could
include the plugin loader class and then add the actual implementation via a `.jar` file in the
libraries section. This did not work as the `.jar` file was not being resolved at runtime.

#### Index File

I looked at the JPF 1.5 plugin and saw an initialization step to load the plugin from the
`META-INF/INDEX.LIST` file. I initially thought that this might fix the `.jar` loading issue but it
didn't work.

```java
// sun.misc.URLClassPath.JarLoader

var11 = new URL(this.csu, var9);
String var12 = URLUtil.urlNoFragString(var11);
if ((var10 = (JarLoader)this.lmap.get(var12)) == null) {
    var10 = (JarLoader)AccessController.doPrivileged(new 3(this, var11), this.acc);
    JarIndex var13 = var10.getIndex();
    if (var13 != null) {
        int var14 = var9.lastIndexOf("/");
        var13.merge(this.index, var14 == -1 ? null : var9.substring(0, var14 + 1));
    }

    this.lmap.put(var12, var10);
}
```

```java
// sun.misc.JarIndex
public static final String INDEX_NAME = "META-INF/INDEX.LIST";
```

You can get the maven-jar-plugin to generate an index file by going to version 2.6 and setting
`<index>true<index>` in the configuration. The documentation suggests that this is deprecated so it
only works on older versions.
