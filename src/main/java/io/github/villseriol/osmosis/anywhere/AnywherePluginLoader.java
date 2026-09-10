// This software is released into the Public Domain.  See copying.txt for details.
package io.github.villseriol.osmosis.anywhere;

import java.util.HashMap;
import java.util.Map;

import org.openstreetmap.osmosis.core.pipeline.common.TaskManagerFactory;
import org.openstreetmap.osmosis.core.plugin.PluginLoader;

import io.github.villseriol.osmosis.anywhere.v0_6.AnywhereTaskFactory;


public class AnywherePluginLoader implements PluginLoader {
    @Override
    public Map<String, TaskManagerFactory> loadTaskFactories() {
        AnywhereTaskFactory transformFactory = new AnywhereTaskFactory();

        Map<String, TaskManagerFactory> tasks = new HashMap<String, TaskManagerFactory>();
        tasks.put("oss-anywhere", transformFactory);
        tasks.put("oss-anywhere-0.6", transformFactory);

        return tasks;
    }
}
