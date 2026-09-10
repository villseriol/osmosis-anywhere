// This software is released into the Public Domain.  See copying.txt for details.
package io.github.villseriol.osmosis.anywhere;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.openstreetmap.osmosis.core.Osmosis;
import org.openstreetmap.osmosis.testutil.AbstractDataTest;


public class AnywherePluginLoaderTest extends AbstractDataTest {

    /**
     * Runs an empty pipeline through the plugin to verify that the loader is
     * discovered via -p and that it registers the oss-anywhere-0.6 task.
     */
    @Test
    public void loadsPluginTask() {
        // @formatter:off
        Osmosis.run(new String[] {
            "-q",
            "-p",
            "io.github.villseriol.osmosis.anywhere.AnywherePluginLoader",
            "--read-empty-0.6",
            "--oss-anywhere-0.6",
            "--write-null" });
        // @formatter:on
    }


    /**
     * Transforms way 314801510, which is only a list of node references with no
     * nodes of its own, and verifies that the way passes through unchanged,
     * matching 314801510-expected.xml.
     *
     * @throws IOException if the output files cannot be compared.
     */
    @Test
    public void leavesWayUnchanged() throws IOException {
        // Plain XML is text, so dataUtils.createDataFile can copy it safely.
        File input = dataUtils.createDataFile("v0_6/314801510.xml");
        File expected = dataUtils.createDataFile("v0_6/314801510-expected.xml");
        File actual = dataUtils.newFile();

        // @formatter:off
        Osmosis.run(new String[] {
            "-q",
            "-p",
            "io.github.villseriol.osmosis.anywhere.AnywherePluginLoader",
            "--read-xml-0.6",
            input.getPath(),
            "--oss-anywhere-0.6",
            "offset=10,20",
            "--write-xml-0.6",
            actual.getPath() });
        // @formatter:on

        dataUtils.compareFiles(expected, actual);
    }


    /**
     * Transforms node 1631266464 at (34.0361200, 134.4857690) and verifies that
     * it is written out shifted by the offset, matching
     * 1631266464-expected.xml.
     *
     * @throws IOException if the output files cannot be compared.
     */
    @Test
    public void shiftsNode() throws IOException {
        // Plain XML is text, so dataUtils.createDataFile can copy it safely.
        File input = dataUtils.createDataFile("v0_6/1631266464.xml");
        File expected = dataUtils.createDataFile("v0_6/1631266464-expected.xml");
        File output = dataUtils.newFile();

        // @formatter:off
        Osmosis.run(new String[] {
            "-q",
            "-p",
            "io.github.villseriol.osmosis.anywhere.AnywherePluginLoader",
            "--read-xml-0.6",
            input.getPath(),
            "--oss-anywhere-0.6",
            "offset=10,20",
            "--write-xml-0.6",
            output.getPath() });
        // @formatter:on

        dataUtils.compareFiles(expected, output);
    }
}
