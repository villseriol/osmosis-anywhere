// This software is released into the Public Domain.  See copying.txt for details.
package io.github.villseriol.osmosis.example;

import org.junit.Test;
import org.openstreetmap.osmosis.core.Osmosis;
import org.openstreetmap.osmosis.testutil.AbstractDataTest;

public class ExamplePluginLoaderTest extends AbstractDataTest {

    /**
     * Runs an empty pipeline through the plugin to verify that the loader is
     * discovered via -p and that it registers the oss-example-0.6 task.
     */
    @Test
    public void loadsPluginTask() {
        // @formatter:off
        Osmosis.run(new String[] {
            "-q",
            "-p",
            "io.github.villseriol.osmosis.example.ExamplePluginLoader",
            "--read-empty-0.6",
            "--oss-example-0.6" });
        // @formatter:on
    }
}
