// This software is released into the Public Domain.  See copying.txt for details.
package io.github.villseriol.osmosis.anywhere.v0_6;

import org.openstreetmap.osmosis.core.pipeline.common.TaskConfiguration;
import org.openstreetmap.osmosis.core.pipeline.common.TaskManager;
import org.openstreetmap.osmosis.core.pipeline.common.TaskManagerFactory;
import org.openstreetmap.osmosis.core.pipeline.v0_6.SinkSourceManager;


public class AnywhereTaskFactory extends TaskManagerFactory {
    @Override
    protected TaskManager createTaskManagerImpl(TaskConfiguration taskConfig) {
        String offset = getStringArgument(taskConfig, "offset", getDefaultStringArgument(taskConfig, "0,0"));

        return new SinkSourceManager(taskConfig.getId(), new AnywhereTask(offset), taskConfig.getPipeArgs());
    }
}
