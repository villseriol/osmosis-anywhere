// This software is released into the Public Domain.  See copying.txt for details.
package io.github.villseriol.osmosis.example;

import java.util.Map;
import java.util.logging.Logger;

import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;


public class ExampleTask implements Sink {
    private static final Logger LOG = Logger.getLogger(ExampleTask.class.getName());

    @Override
    public void initialize(Map<String, Object> metaData) {
        LOG.info("initialize");
    }


    @Override
    public void complete() {
        LOG.info("complete");
    }


    @Override
    public void close() {
        LOG.info("close");
    }


    @Override
    public void process(EntityContainer entityContainer) {
        LOG.info("process");
    }

}
